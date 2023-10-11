package com.hariyali.serviceimpl;

import com.hariyali.EnumConstants;
import com.hariyali.config.JwtHelper;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PaginationRequestDTO;
import com.hariyali.dto.PlantationMasterDTO;
import com.hariyali.entity.Plantation;
import com.hariyali.entity.PlantationMaster;
import com.hariyali.entity.SeasonMonth;
import com.hariyali.entity.UserPackages;
import com.hariyali.exceptions.CustomException;
import com.hariyali.repository.PlantationMasterRepository;
import com.hariyali.repository.PlantationRepository;
import com.hariyali.repository.SeasonMonthRepository;
import com.hariyali.repository.UserPackageRepository;
import com.hariyali.service.PlantationMasterService;
import com.hariyali.utils.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class PlantationMasterServiceImpl implements PlantationMasterService{

    @Autowired
    private PlantationMasterRepository repository;

    @Autowired
    private UserPackageRepository userPackageRepository;

    @Autowired
    private SeasonMonthRepository seasonMonthRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PlantationRepository plantationRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private EmailService emailService;

    @Value("${donation.approval-extension}")
    Integer extensionDays;

    @Override
    public ApiResponse<List<PlantationMasterDTO>> findAllByFilter(PaginationRequestDTO<PlantationMasterDTO> dto) {
        ApiResponse<List<PlantationMasterDTO>> response = new ApiResponse<>();
        PlantationMasterDTO masterDTO = dto.getData();
        List<String> cityList = Arrays.asList(masterDTO.getVillage().split(","));
        List<String> districtList = Arrays.asList(masterDTO.getDistrict().split(","));
        Pageable pageable = PageRequest.of(dto.getPageNumber(), dto.getPageSize());
        Page<PlantationMaster> result= repository.findAllByFilter(masterDTO.getSeason(), cityList, districtList, masterDTO.getPlantationYear(), pageable);
        if (!isNull(result) && !result.getContent().isEmpty()) {
            List<PlantationMasterDTO> plantationMasterDTOS = of(result.getContent()).get().stream()
                    .map(entity -> {
                        PlantationMasterDTO plantationMasterDTO = modelMapper.map(entity, PlantationMasterDTO.class);
                        plantationMasterDTO.setPlantationDateString(toDateString(plantationMasterDTO.getPlantationDate(), "dd/MM/yyyy"));
                        return plantationMasterDTO;
                    }).collect(Collectors.toList());
            response.setData(plantationMasterDTOS);
            response.setTotalPages(result.getTotalPages());
            response.setTotalRecords(result.getTotalElements());
            response.setStatus(EnumConstants.SUCCESS);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Data fetched successfully.");
            return response;
        } else
            throw new CustomException("No record found for given filter");
    }

    @Override
    @Transactional
    public ApiResponse<PlantationMasterDTO> upload(MultipartFile multipartFile, HttpServletRequest request) {
        ApiResponse<PlantationMasterDTO> response = new ApiResponse<>();
        if (Objects.equals(multipartFile.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                || Objects.equals(multipartFile.getContentType(), "application/vnd.ms-excel")) {
            try {
                String userName = jwtHelper.getUsernameFromToken(request.getHeader("Authorization").substring(7));
                List<PlantationMasterDTO> dtoList = readExcel(multipartFile.getInputStream());
                List<PlantationMaster> entityList = dtoList.stream()
                        .map(dto -> {
                            PlantationMaster plantationMaster = modelMapper.map(dto, PlantationMaster.class);
                            plantationMaster.setCreated_by(userName);
                            plantationMaster.setCreated_date(new Date());
                            return plantationMaster;
                        }).collect(Collectors.toList());
                entityList = repository.saveAll(entityList);

                Map<String, List<PlantationMaster>> seasonMap = entityList.stream()
                        .collect(Collectors.groupingBy(PlantationMaster ::getSeason, Collectors
                                .mapping(dto -> dto, Collectors.toList())));
                seasonMap.forEach((key, value) -> {
                    List<SeasonMonth> seasonMonths = seasonMonthRepository.findBySeasonOrderByMonthNumber(key);
                    ofNullable(seasonMonths).filter(data -> !data.isEmpty()).orElseThrow(()-> new CustomException("season month configuration not found"));

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.MONTH, seasonMonths.get(0).getMonthNumber()-1);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    Date fromDate = cal.getTime();

                    cal.set(Calendar.MONTH, seasonMonths.get(seasonMonths.size()-1).getMonthNumber()-1);
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
                    Date toDate = cal.getTime();

                    //get approval date
                    cal.set(Calendar.MONTH, seasonMonths.get(seasonMonths.size()-1).getMonthNumber());
                    cal.set(Calendar.DAY_OF_MONTH, extensionDays);

                    Date approvalDate = cal.getTime();

                    List<UserPackages> userPackages = userPackageRepository.findAllPendingPackages(fromDate, toDate, approvalDate);
                    if(!isNull(userPackages) && !userPackages.isEmpty()) {
                        toPlantations(userPackages, value, userName);
                    }
                });
                response.setStatus("Success");
                response.setMessage("File uploaded successfully.");
                return  response;
            } catch (IOException e){
                log.error("Exception  = "+ e);
                throw new CustomException("Error while uploading.");
            }

        } else{
            throw new CustomException("Invalid file, Please upload excel file.");
        }
    }//method

    private List<Plantation> toPlantations(List<UserPackages> userPackages, List<PlantationMaster> plantationMasters, String createdBy){
        List<UserPackages> updatedUserPackages = new ArrayList<>();
        List<Plantation> plantationList = plantationMasters.stream().map(plantationMaster -> {
            Long noOfPlantsPlanted = plantationMaster.getNoOfPlantsPlanted();
            List<Plantation> plantations = new ArrayList<>();
//            while(noOfPlantsPlanted > 0) {
                for (UserPackages packages: userPackages) {
                    if(noOfPlantsPlanted > 0) {
                        if (ofNullable(packages.getPlantAllocated()).orElse(0) <= ofNullable(packages.getNoOfBouquets()).orElse(0) && ofNullable(packages.getNoOfBouquets()).orElse(0) != 0) {
                            Integer allocatedPlant = ofNullable(packages.getPlantAllocated()).orElse(0);
                            if ((noOfPlantsPlanted - (packages.getNoOfBouquets() - allocatedPlant)) > 0) {
                                allocatedPlant = packages.getNoOfBouquets() - allocatedPlant;
                                packages.setPlanted(true);
                               emailService.sendPlantationMail(packages, plantationMaster);
                            } else {
                                packages.setPlanted(false);
                                allocatedPlant = noOfPlantsPlanted.intValue();
                                emailService.sendPlantationMail(packages, plantationMaster);
                            }
                            noOfPlantsPlanted -= allocatedPlant;
                            packages.setPlantAllocated(allocatedPlant);

                            updatedUserPackages.add(packages);
                            plantations.add(toPlantation(packages, plantationMaster, allocatedPlant, createdBy));
                        }//if
                    }
                }//for

//            }//while
            return plantations;
        }).flatMap(Collection::stream).collect(Collectors.toList());
        if(!updatedUserPackages.isEmpty()) {
            userPackageRepository.saveAll(updatedUserPackages);
        }
        if(!plantationList.isEmpty()) {
            plantationList = plantationRepository.saveAll(plantationList);
        }
        return plantationList;
    }

    private Plantation toPlantation(UserPackages packages, PlantationMaster plantationMaster, Integer noOfPlantPlanted, String createdBy){
        Plantation plantation = new Plantation();
        plantation.setPlantationMaster(plantationMaster);
        plantation.setUserPackages(packages);
        plantation.setDonation(packages.getUserDonation());
        plantation.setNoOfPlantsPlanted(noOfPlantPlanted);
        plantation.setYear1Report(false);
        plantation.setYear2Report(false);
        plantation.setCreatedBy(createdBy);
        return plantation;
    }

    private List<PlantationMasterDTO> readExcel(InputStream stream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(stream);
        XSSFSheet worksheet = workbook.getSheetAt(0);
        if(worksheet.getPhysicalNumberOfRows() < 2){
            throw new CustomException("Excel is empty, Please fill excel with correct data and re-upload");
        }
        log.info("No of Record exist ::" + worksheet.getPhysicalNumberOfRows());
        log.info("Last Row: " + worksheet.getLastRowNum());
        int i = 1;
        List<PlantationMasterDTO> dtoList = new ArrayList<>();
        while (i <= worksheet.getLastRowNum()) {
            XSSFRow row = worksheet.getRow(i++);
            int rowNumber = i+1;
            PlantationMasterDTO dto = new PlantationMasterDTO();
            dto.setState(ofNullable(row.getCell(0)).map(String::valueOf).map(String::trim)
                    .orElseThrow(()-> new CustomException("State is invalid or empty in row no "+ rowNumber+" column no 1")));
            dto.setDistrict(ofNullable(row.getCell(1)).map(String::valueOf).map(String::trim)
                            .orElseThrow(()-> new CustomException("District is invalid or empty in row no "+ rowNumber+" column no 1")));
            dto.setVillage(ofNullable(row.getCell(2)).map(String::valueOf).map(String::trim)
                            .orElseThrow(()-> new CustomException("Village is invalid or empty in row no "+ rowNumber+" column no 2")));
            dto.setSeason(ofNullable(row.getCell(3)).map(String::valueOf).map(String::trim)
                            .orElseThrow(()-> new CustomException("Season is invalid or empty in row no "+ rowNumber+" column no 3")));
            dto.setPlot(ofNullable(row.getCell(4)).map(String::valueOf).map(String::trim)
                            .orElseThrow(()-> new CustomException("Plat is invalid or empty in row no "+ rowNumber+" column no 4")));
            dto.setNoOfPlantsPlanted(ofNullable(row.getCell(5)).map(XSSFCell::getRawValue).map(Long::parseLong)
                            .orElseThrow(()-> new CustomException("No of plats planted is invalid or empty in row no "+ rowNumber+" column no 5")));
            Optional<Date> plantationDate = ofNullable(row.getCell(6)).map(String::valueOf).map(String::trim)
                    .map(date ->toDate(date, "dd-MM-yyyy"));
            dto.setPlantationDate(plantationDate.isPresent() ? plantationDate.get() : ofNullable(row.getCell(6)).map(XSSFCell::getDateCellValue).orElseThrow(()-> new CustomException("PlantationDate is invalid or empty in row no "+ rowNumber+" column no 6, required in dd-MM-yyyy format")));
            dto.setLatitude(ofNullable(row.getCell(7)).map(XSSFCell::getRawValue).map(Double::valueOf)
                            .orElseThrow(()-> new CustomException("Latitude is invalid or empty in row no "+ rowNumber+" column no 7")));
            dto.setLongitude(ofNullable(row.getCell(8)).map(XSSFCell::getRawValue).map(Double::valueOf)
                            .orElseThrow(()-> new CustomException("Longitude is invalid or empty in row no "+ rowNumber+" column no 8")));
            dto.setStatus(ofNullable(row.getCell(9)).map(String::valueOf).map(String::trim)
                            .orElseThrow(()-> new CustomException("Status is invalid or empty in row no "+ rowNumber+" column no 9")));
            dtoList.add(dto);
        }
        return dtoList;
    }

    private Date toDate(String dateString, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            log.error("Invalid date format, please provide date in "+ dateFormat);
        }
        return null;
    }

    private String toDateString(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }//method

    private void sendMail(String mailId, String plantationDate) {
        String subject = "Update about plantation";
        String bodyMessage = "your plantation is done \n and plantation date is "+ plantationDate;
        emailService.sendPlantationEmail(mailId, subject, bodyMessage);
    }//methods

    @Override
    public ByteArrayInputStream export(PlantationMasterDTO masterDTO) {

        List<String> cityList = Arrays.asList(masterDTO.getVillage().split(","));
        List<String> districtList = Arrays.asList(masterDTO.getDistrict().split(","));

        List<PlantationMaster> plantationMasters= repository.findAllByFilter(masterDTO.getSeason(), cityList, districtList,  masterDTO.getPlantationYear());
        Workbook workbook = new SXSSFWorkbook();

        try {
            Sheet sheet = workbook.createSheet("User Plant Report ");

            Row row = sheet.createRow(0);
            CellStyle style = workbook.createCellStyle();
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setBold(true);
            font.setFontHeight(12);
            style.setFont(font);

            // Set the background color directly (YELLOW)
            style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            style.setFillPattern((short) FillPatternType.SOLID_FOREGROUND.ordinal());

            Cell cell = row.createCell(0);
            cell.setCellValue("State");
            sheet.autoSizeColumn(0);
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue("District");
            sheet.autoSizeColumn(1);
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue("City");
            sheet.autoSizeColumn(2);
            cell.setCellStyle(style);

            cell = row.createCell(3);
            cell.setCellValue("Season");
            sheet.autoSizeColumn(3);
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue("Plot");
            sheet.autoSizeColumn(4);
            cell.setCellStyle(style);

            cell = row.createCell(5);
            cell.setCellValue("NoOfPlantsPlanted");
            sheet.autoSizeColumn(5);
            cell.setCellStyle(style);

            cell = row.createCell(6);
            cell.setCellValue("Plantation Date");
            sheet.autoSizeColumn(6);
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue("Latitude");
            sheet.autoSizeColumn(7);
            cell.setCellStyle(style);

            cell = row.createCell(8);
            cell.setCellValue("Longitude");
            sheet.autoSizeColumn(8);
            cell.setCellStyle(style);

            cell = row.createCell(9);
            cell.setCellValue("Status");
            sheet.autoSizeColumn(9);
            cell.setCellStyle(style);


            // Auto-size columns
            for (int i = 0; i <= 9; i++) {
                sheet.autoSizeColumn(i);
            }

            AtomicInteger rowNumber = new AtomicInteger(1);
            plantationMasters.forEach(plantationMaster ->  {

                Row dataRow = sheet.createRow(rowNumber.getAndIncrement());
                Cell dataCell = dataRow.createCell(0);
                dataCell.setCellValue(plantationMaster.getState());
                sheet.autoSizeColumn(0);

                dataCell = dataRow.createCell(1);
                dataCell.setCellValue(plantationMaster.getDistrict());
                sheet.autoSizeColumn(0);

                dataCell = dataRow.createCell(2);
                dataCell.setCellValue(plantationMaster.getVillage());
                sheet.autoSizeColumn(0);

                dataCell = dataRow.createCell(3);
                dataCell.setCellValue(plantationMaster.getSeason());
                sheet.autoSizeColumn(0);

                dataCell = dataRow.createCell(4);
                dataCell.setCellValue(plantationMaster.getPlot());
                sheet.autoSizeColumn(0);

                dataCell = dataRow.createCell(5);
                dataCell.setCellValue(plantationMaster.getNoOfPlantsPlanted());
                sheet.autoSizeColumn(0);

                dataCell = dataRow.createCell(6);
                dataCell.setCellValue(toDateString(plantationMaster.getPlantationDate(), "dd/MM/yyyy"));
                sheet.autoSizeColumn(0);

                dataCell = dataRow.createCell(7);
                dataCell.setCellValue(plantationMaster.getLatitude());
                sheet.autoSizeColumn(0);

                dataCell = dataRow.createCell(8);
                dataCell.setCellValue(plantationMaster.getLongitude());
                sheet.autoSizeColumn(0);

                dataCell = dataRow.createCell(9);
                dataCell.setCellValue(plantationMaster.getStatus());
                sheet.autoSizeColumn(0);
            });

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (Exception e) {
            log.error("Exception = "+e);
        }
        return null;
    }//method

    @Override
    public ByteArrayInputStream downloadTemplate() {
        Workbook workbook = new SXSSFWorkbook();

        try {
            Sheet sheet = workbook.createSheet("User Plant Report ");

            Row row = sheet.createRow(0);
            CellStyle style = workbook.createCellStyle();
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setBold(true);
            font.setFontHeight(12);
            style.setFont(font);

            // Set the background color directly (YELLOW)
            style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            style.setFillPattern((short) FillPatternType.SOLID_FOREGROUND.ordinal());

            Cell cell = row.createCell(0);
            cell.setCellValue("State");
            sheet.autoSizeColumn(0);
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue("District");
            sheet.autoSizeColumn(1);
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue("City");
            sheet.autoSizeColumn(2);
            cell.setCellStyle(style);

            cell = row.createCell(3);
            cell.setCellValue("Season");
            sheet.autoSizeColumn(3);
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue("Plot");
            sheet.autoSizeColumn(4);
            cell.setCellStyle(style);

            cell = row.createCell(5);
            cell.setCellValue("NoOfPlantsPlanted");
            sheet.autoSizeColumn(5);
            cell.setCellStyle(style);

            cell = row.createCell(6);
            cell.setCellValue("Plantation Date");
            sheet.autoSizeColumn(6);
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue("Latitude");
            sheet.autoSizeColumn(7);
            cell.setCellStyle(style);

            cell = row.createCell(8);
            cell.setCellValue("Longitude");
            sheet.autoSizeColumn(8);
            cell.setCellStyle(style);

            cell = row.createCell(9);
            cell.setCellValue("Status");
            sheet.autoSizeColumn(9);
            cell.setCellStyle(style);


            // Auto-size columns
            for (int i = 0; i <= 9; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (Exception e) {
            log.error("Exception = "+e);
        }
        return null;
    }//method 

    @Override
    public ApiResponse<List<Integer>> findByDistinctYears() {
        ApiResponse<List<Integer>> response = new ApiResponse<>();
        response.setStatus("Success");
        response.setMessage("Success");
        response.setData(repository.findByDistinctYears());
        return response;
    }//method

    @Override
    public ApiResponse<List<String>> findByDistinctSeason() {
        ApiResponse<List<String>> response = new ApiResponse<>();
        response.setStatus("Success");
        response.setMessage("Success");
        response.setData(repository.findByDistinctSeason());
        return response;
    }

    @Override
    public ApiResponse<List<String>> findByDistinctDistricts(Integer year) {
        ApiResponse<List<String>> response = new ApiResponse<>();
        response.setStatus("Success");
        response.setMessage("Success");
        response.setData(repository.findByDistinctDistricts(year));
        return response;
    }//method

    @Override
    public ApiResponse<List<String>> findByDistinctCities(Integer year) {
        ApiResponse<List<String>> response = new ApiResponse<>();
        response.setStatus("Success");
        response.setMessage("Success");
        response.setData(repository.findByDistinctCities(year));
        return response;
    }//method

    @Override
	public String sendPlantationYear1Report(long plantationId,HttpServletRequest request) {
		Date currentDate = new Date();
		String token = request.getHeader("Authorization");
		String userName = jwtHelper.getUsernameFromToken(token.substring(7));
		Plantation plant=plantationRepository.getById(plantationId);
		emailService.sendFirstAnnualPlantationMail(plant);
		plant.setYear1Report(true);
		plant.setYear1ReportBy(userName);
		plant.setYear1ReportDate(currentDate);
		plantationRepository.save(plant);
		return "Year 1 report send successfully";
	}
	
	@Override
	public String sendPlantationYear2Report(long plantationId,HttpServletRequest request) {
		Date newDate = new Date();
		String token = request.getHeader("Authorization");
		String userName = jwtHelper.getUsernameFromToken(token.substring(7));
		Plantation plant=plantationRepository.getById(plantationId);
		emailService.sendSecondAnnualPlantationMail(plant);
		plant.setYear2Report(true);
		plant.setYear2ReportBy(userName);
		plant.setYear2ReportDate(newDate);
		plantationRepository.save(plant);
		return "Year 2 report send successfully";
	}

}//class

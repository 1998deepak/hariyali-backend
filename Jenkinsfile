@Library(['mm-dsl'])_
mm_java_build
{
    AGENT                   = ""
}
mm_docker_build
{
    REGISTRY_NAME           = "modernization-dev-832770"
    IMAGE_NAME              = "hariyalibackend"
    CLOUD                   = "gcp"
    AGENT                   = ""
}
mm_k8s_deployment_pipeline_gcp
{
    PROJECT                 = "modernization-dev-832770"
    REGION                  = "asia-south1-a"
    CLUSTER_NAME            = "mgcmdmodkew02-gke-k8s"
    ENVIRONMENT             = "dev"
    DEPLOYMENT_NAME         = "hariyalibackend"
    DEPLOYMENT_TYPE         = "automatic"
    AGENT                   = "jenkins-gcp-dev-agent2"
    RESTRICT_TO_BRANCH      = "gcp_dev"
}
mm_k8s_deployment_pipeline_gcp
{
    PROJECT                 = "modernization-uatx-505932"
    REGION                  = "asia-south1-a"
    CLUSTER_NAME            = "mgcmumodkew02-gke-k8s"
    ENVIRONMENT             = "uat"
    DEPLOYMENT_NAME         = "hariyalibackend"
    DEPLOYMENT_TYPE         = "automatic"
    AGENT                   = "jenkins-gcp-uat-agent"
    RESTRICT_TO_BRANCH      = "gcp_uat"
}
mm_k8s_deployment_pipeline_gcp
{
    PROJECT                 = "modernization-prod-566335"
    REGION                  = "asia-south1-a"
    CLUSTER_NAME            = "mgcmpmodkew02-gke-k8s"
    ENVIRONMENT             = "prod"
    DEPLOYMENT_NAME         = "hariyalibackend"
    DEPLOYMENT_TYPE         = "automatic"
    AGENT                   = "jenkins-gcp-prod-agent"
    RESTRICT_TO_BRANCH      = "gcp_prod"
}
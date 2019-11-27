package no.mats.deployments

def createDeployCommand(options) {
  def deployCmd = "s3-deploy '${options.s3DeployFolder}/**'"

  deployCmd += " --cwd '${options.s3DeployFolder}/'"
  deployCmd += " --region ${options.s3Region}"
  deployCmd += " --bucket ${options.s3Bucket}"
  deployCmd += " --private"

  if (options.s3CloudFrontDistId != null) {
    deployCmd += "--distId ${options.s3CloudFrontDistId} --invalidate '/'"
  }

  return deployCmd
}

def validateOptions(options) {

  if (options.s3DeployFolder == null) {
    error("Missing option: s3DeployFolder")
  }

  if (options.s3Region == null) {
    error("Missing option: s3Region")
  }

  if (options.s3Bucket == null) {
    error("Missing option: s3Bucket")
  }
}

def deploy(options, nodeImage) {

  validateOptions(options)

  withCredentials([
    usernamePassword(
      credentialsId: "serverless-provider-1",
      usernameVariable: "KEY",
      passwordVariable: "SECRET",
    )
  ]) {
    nodeImage.inside("-e AWS_ACCESS_KEY_ID=${KEY} -e AWS_SECRET_ACCESS_KEY=${SECRET}") {
      sh("npm i -g s3-deploy")
      sh(createDeployCommand(options))
    }
  }
}
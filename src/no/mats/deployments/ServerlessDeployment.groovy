package no.mats.deployments

def deploy(nodeImage) {

  withCredentials([
    usernamePassword(
      credentialsId: "serverless-provider-1",
      usernameVariable: "KEY",
      passwordVariable: "SECRET",
    )
  ]) {
    nodeImage.inside("-e AWS_ACCESS_KEY_ID=${KEY} -e AWS_SECRET_ACCESS_KEY=${SECRET}") {
      sh("node_modules/serverless/bin/serverless deploy")
    }
  }
}
package no.mats.deployments

def deploy(nodeImage) {

  withCredentials([
    usernamePassword(
      credentialsId: "serverless-provider-1",
      usernameVariable: "KEY",
      passwordVariable: "SECRET",
    )
  ]) {
    nodeImage.inside() {

      def serverless = "node_modules/serverless/bin/serverless"

      sh("${serverless} config credentials --provider aws --key ${KEY} --secret ${SECRET}")
      sh("cat ~/.aws/credentials")
      
      try {
        sh("${serverless} deploy")
      } finally {
        sh("${serverless} config credentials --provider aws --key gibberish --secret gibberish --overwrite")
      }
    }
  }
}
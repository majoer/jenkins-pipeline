withCredentials([
  usernamePassword(
    credentialsId: "serverless-provider-1",
    passwordVariable: "KEY",
    usernameVariable: "SECRET"
  )
]) {
  def serverless = "node_modules/serverless/bin/serverless"

  sh("${serverless} config credentials --provider aws --key ${KEY} --secret ${SECRET}")
  
  try {
    sh("${serverless} deploy")
  } finally {
    sh("${serverless} config credentials --provider aws --key gibberish --secret gibberish")
  }
}
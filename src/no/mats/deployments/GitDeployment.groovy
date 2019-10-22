package no.mats.deployments

def deploy() {

  withCredentials([
    usernamePassword(
      credentialsId: "git-provider-1",
      usernameVariable: "GIT_USERNAME",
      passwordVariable: "GIT_PASSWORD"
    )
  ]) {
    def origin = sh(returnStdout: true, script: "git remote get-url origin")
    def gitUrl = origin.replace("https://", "")
    def pushUrl = "https://${GIT_USERNAME}:${GIT_PASSWORD}@${gitUrl}"

    sh('git checkout -B release/beta') 
    sh("git push ${pushUrl}")
  }

}
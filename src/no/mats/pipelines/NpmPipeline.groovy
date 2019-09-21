package no.mats.pipelines;

def run(Map<String, String> options) {
  node('master') {

    stage('Install') {
      echo 'Install'
      sh 'npm i'
    }
  }
}
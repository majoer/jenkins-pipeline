package no.mats.pipelines;

def run(Map<String, String> options) {
  node('nodejs') {

    stage('Install') {
      echo 'Install'
      sh 'npm i'
    }
  }
}
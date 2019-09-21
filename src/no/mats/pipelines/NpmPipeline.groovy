package no.mats.pipelines;

def run(Map<String, String> options) {
  stage('Install') {
    steps {
      echo 'Install'
      sh 'npm i'
    }
  }
}
package no.mats.pipelines;

def run(Map<String, String> options) {
  stage('Install') {
    echo 'Install'
    sh 'npm i'
  }
}
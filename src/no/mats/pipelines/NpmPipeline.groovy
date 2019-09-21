package no.mats.pipelines;

class NpmPipeline {

  NpmPipeline(Map<String, String> options) {

    stage ('Install') {
      echo 'Install'

      sh 'npm i'
    }
  }
}
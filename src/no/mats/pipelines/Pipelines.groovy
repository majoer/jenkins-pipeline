package no.mats.pipelines;

class Pipelines {

  static void npmPipeline(Map<String, String> options) {
    new NpmPipeline(options);
  }
}
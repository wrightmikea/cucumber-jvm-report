package net.masterthought.jenkins;

import net.masterthought.jenkins.json.Closure;
import net.masterthought.jenkins.json.Element;
import net.masterthought.jenkins.json.Step;
import net.masterthought.jenkins.json.Util;

import java.util.ArrayList;
import java.util.List;


public class TagObject {
    
    private String tagName;
    private List<ScenarioTag> scenarios = new ArrayList<ScenarioTag>();
    private List<Element> elements = new ArrayList<Element>();

    public String getTagName() {
        return tagName;
    }

    public String getFileName(){
        return tagName.replace("@","").trim() + ".html";
    }

    public List<ScenarioTag> getScenarios() {
        return scenarios;
    }
    
    public void setScenarios(List<ScenarioTag> scenarioTagList){
        this.scenarios = scenarioTagList;
    }

    public TagObject(String tagName, List<ScenarioTag> scenarios){
        this.tagName = tagName;
        this.scenarios = scenarios;
    }

    private void getElements() {
        for(ScenarioTag scenarioTag : scenarios){
            elements.add(scenarioTag.getScenario());
        }
    }

    public Integer getNumberOfScenarios(){
        return this.scenarios.size();
    }

    public String getDurationOfSteps() {
        Long duration = 0L;
        for (ScenarioTag scenarioTag : scenarios) {
          for (Step step : scenarioTag.getScenario().getSteps()) {
                    duration = duration + step.getDuration();
          }
        }
        return Util.formatDuration(duration);
    }

    public int getNumberOfSteps(){
        int totalSteps = 0;
        for(ScenarioTag scenario : scenarios){
            totalSteps += scenario.getScenario().getSteps().length;
        }
        return totalSteps;
    }

    public int getNumberOfPasses() {
        return Util.findStatusCount(getStatuses(), Util.Status.PASSED);
    }

    public int getNumberOfFailures() {
        return Util.findStatusCount(getStatuses(), Util.Status.FAILED);
    }

    public int getNumberOfSkipped()  {
        return Util.findStatusCount(getStatuses(), Util.Status.SKIPPED);
    }

    public int getNumberOfPending()  {
          return Util.findStatusCount(getStatuses(), Util.Status.UNDEFINED);
      }
    
    private List<Util.Status> getStatuses() {
        List<Util.Status> statuses = new ArrayList<Util.Status>();
          for(ScenarioTag scenarioTag : scenarios){
                
                    for (Step step : scenarioTag.getScenario().getSteps()) {
                        statuses.add(step.getStatus());
                    }
          }
        return statuses;
    }

    public Util.Status getStatus() {
        getElements();
        Closure<String, Element> scenarioStatus = new Closure<String, Element>() {
            public Util.Status call(Element step) {
                return step.getStatus();
            }
        };
        
        Element[] elementList = new Element[elements.size()];
        List<Util.Status> results = Util.collectScenarios(elements.toArray(elementList), scenarioStatus);
        return results.contains(Util.Status.FAILED) ? Util.Status.FAILED : Util.Status.PASSED;
    }

    public String getRawStatus() {
        return getStatus().toString().toLowerCase();
    }
    
}

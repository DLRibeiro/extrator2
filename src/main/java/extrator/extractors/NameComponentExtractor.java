package extrator.extractors;

import extrator.ExtractorConstants;
import extrator.PropertiesUtil;
import extrator.entities.MergeScenario;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;

public class NameComponentExtractor extends SimpleStringComponentExtractor implements
    Extractor<MergeScenario> {

  private ProjectClusterizer clusterizer;
  private List<String> projectPaths;
  private int currentProjectPosition;

  public NameComponentExtractor(List<String> componentWords,
      List<String> excludedWords) {
    super(componentWords, excludedWords);
    Properties properties = new Properties();
    try {
      PropertiesUtil.loadProperties(properties);
    } catch (IOException e) {
      e.printStackTrace();
    }
    String projectPaths = properties.getProperty(ExtractorConstants.PROJECT_PATH_PROPERTY);
    this.projectPaths = this.generateProjectPaths(projectPaths);
    this.currentProjectPosition = 0;
    this.clusterizer = new ProjectClusterizer(this.projectPaths.get(0));
    this.clusterizer.clusterProject();
  }

  private List<String> generateProjectPaths(String projectPaths){
    List<String> listProjectPaths = new ArrayList<>();
    String[] arrayProjectPaths = projectPaths.replace("[","").replace("]","").split("\\|");
    for(String projectPath : arrayProjectPaths){
      String trimmedProjectPath = projectPath.trim();
      if(!trimmedProjectPath.equals("")){
        listProjectPaths.add(trimmedProjectPath);
      }
    }
    return listProjectPaths;
  }

  @Override
  protected List<String> extractComponentsFromFileList(String fileList) {
    Set<String> selectedComponents = new HashSet<>();
    List<String> javaFiles = getCleanJavaFiles(fileList);

    for (String javaFile : javaFiles) {
      try {
        List<String> componentsFromFile = this.clusterizer.getClusterComponents().get(javaFile);
        if (componentsFromFile == null) {
          this.clusterizer.addNewFileCluster(javaFile);
        }
        selectedComponents.addAll(this.clusterizer.getClusterComponents().get(javaFile));
      } catch (NullPointerException e) {
        System.out.println("Did not found :" + javaFile);
      }
    }
    List<String> selectedComponentsList = new ArrayList<>();
    for (String component : selectedComponents) {
      selectedComponentsList.add(component);
    }
    this.changeProjectClusterizer(this.currentProjectPosition+1);
    return selectedComponentsList;
  }

  private void changeProjectClusterizer(int projectPosition){
    if(projectPosition < this.projectPaths.size()) {
      this.clusterizer = new ProjectClusterizer(this.projectPaths.get(projectPosition));
      this.currentProjectPosition = projectPosition;
    }
  }

  protected String cleanForStopWord(String file) {
    String cleanFileName = FilenameUtils.getName(file);
    for (String stopWord : this.excludedWords) {
      cleanFileName = cleanFileName.replace(stopWord, "");
    }

    for (String componentWord : this.componentWords) {
      cleanFileName = cleanFileName.replace(componentWord, "");
    }
    return cleanFileName;
  }

  @Override
  protected List<String> getCleanJavaFiles(String fileList) {
    List<String> javaFiles = this.getJavaFiles(fileList);
    List<String> cleanJavaFiles = new ArrayList<>();
    for (String javaFile : javaFiles) {
      String cleanJavaFile = FilenameUtils.getName(javaFile.replace(".java", ""));
      cleanJavaFiles.add(cleanJavaFile);
    }
    return cleanJavaFiles;
  }
}
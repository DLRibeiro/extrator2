package extrator.extractors;

import static extrator.PropertiesUtil.getStringList;

import extrator.ExtractorConstants;
import java.io.IOException;
import java.util.List;

public class ExtractorFactory {

  public static Extractor createExtractor(String extractorType){
    Extractor extractor = null;
    try {
      switch (extractorType) {
        case ExtractorConstants
            .NAME_EXTRACTOR:
          extractor = ExtractorFactory.buildNameComponentExtractor();
        break;
        default: extractor = ExtractorFactory.buildSimpleStringComponentExtractor();
        break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return extractor;
  }

  private static Extractor buildSimpleStringComponentExtractor() throws IOException {
    List<String> listStopWords = getStringList("stopWords.txt");
    List<String> listComponentWords = getStringList("componentWords.txt");
    return new SimpleStringComponentExtractor(listComponentWords, listStopWords);
  }

  private static Extractor buildNameComponentExtractor() throws IOException {
    List<String> listStopWords = getStringList("stopWords.txt");
    List<String> listComponentWords = getStringList("componentWords.txt");
    return new NameComponentExtractor(listComponentWords, listStopWords);
  }

}

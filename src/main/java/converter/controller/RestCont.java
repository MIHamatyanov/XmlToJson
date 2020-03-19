package converter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import converter.service.XmlToJsonConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@RestController
public class RestCont {
    private XmlToJsonConverter converter;

    public RestCont(XmlToJsonConverter converter) {
        this.converter = converter;
    }

    @PostMapping("/")
    public Object handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, SAXException, ParserConfigurationException {
        String str = converter.convert(file.getBytes()).toString();
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.readValue(str, Object.class);
    }
}

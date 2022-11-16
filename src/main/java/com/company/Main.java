package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        //CSV->JSON
        String fileName_csv = "data.csv";
        String fileName_csv_json = "data_csv.json";
        List<Employee> list = parseCSV(columnMapping, fileName_csv);
        String json = listToJson(list);
        writeString(json, fileName_csv_json);
        //XML->JSON
        String fileName_xml = "data2.xml";
        String fileName_xml_json = "data_xml.json";
        List<Employee> list2 = parseXML(columnMapping, fileName_xml);
        String json2 = listToJson(list2);
        writeString(json2, fileName_xml_json);
    }

    private static List<Employee> parseXML(String[] columnMapping, String fileName_xml) {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            Document doc = builder.parse(new File(fileName_xml));
            Element root = doc.getDocumentElement();
            read(root, list);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static void read(Node root, List<Employee> list) {
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (node_ instanceof Element) {
                if (node_.hasChildNodes()) {
                    Employee employee = new Employee();
                    NodeList childList = node_.getChildNodes();
                    for (int a = 0; a < childList.getLength(); a++) {
                        Node child_ = childList.item(a);
                        if (child_ instanceof Element) {
                            if (!child_.getTextContent().trim().isEmpty() && !((Text) child_.getFirstChild()).getData().trim().isEmpty()
                                    && !((Text) child_.getFirstChild()).getData().trim().equals("\n")) {
                                switch (child_.getNodeName()) {
                                    case "id": {
                                        employee.id = Long.valueOf(child_.getTextContent());
                                        break;
                                    }
                                    case "firstName": {
                                        employee.firstName = child_.getTextContent();
                                        break;
                                    }
                                    case "lastName": {
                                        employee.lastName = child_.getTextContent();
                                        break;
                                    }
                                    case "country": {
                                        employee.country = child_.getTextContent();
                                        break;
                                    }
                                    case "age": {
                                        employee.age = Integer.valueOf(child_.getTextContent());
                                    }
                                }
                            }
                        }
                    }
                    list.add(employee);
                }
            }
        }
    }

    private static void writeString(String json, String fileName_json) {
        try (FileWriter file = new
                FileWriter(fileName_json)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String csv_json = gson.toJson(list, listType);
        return csv_json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}

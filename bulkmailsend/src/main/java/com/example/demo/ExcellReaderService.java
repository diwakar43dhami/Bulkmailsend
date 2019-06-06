package com.example.demo;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

/**
 * Created by Diwakar on 5/23/2019.
 */
@Component
public class ExcellReaderService {
    @Autowired
    MailService mailService;
//    public static final String SAMPLE_XLSX_FILE_PATH = "C:\\Users\\Diwakar\\Desktop\\Book8.xlsx";

    public List<User> readEmailFromExcel(MultipartFile file) {
        List<String> headers = new ArrayList<String>();
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(String.valueOf(file)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        workbook.forEach(sheet -> {
            System.out.println("=> " + sheet.getSheetName());
            System.out.println(sheet);
        });
        Sheet sheet = workbook.getSheetAt(0);
        List<User> users = new ArrayList<>();
        sheet.forEach(row -> {
            User user = new User();
            Map<String, String> userMap = new LinkedHashMap<String, String>();
            row.forEach(cell -> {
                System.out.println(row);
                Integer columnIndex = cell.getColumnIndex();
                Integer rowIndex = cell.getRowIndex();

                if (rowIndex == 0) {
                    headers.add(cell.getStringCellValue());
                } else {
                    if (columnIndex == 1) {
                        if (cell.getCellTypeEnum() == CellType.STRING) {
                            user.setPassWord(cell.getStringCellValue());
                        }else if(cell.getCellTypeEnum() ==CellType.NUMERIC){
                            user.setPassWord(String.valueOf((int)cell.getNumericCellValue()));
                        }
                    }
                    if (columnIndex == 2) {
                        user.setEmailAddress(cell.getStringCellValue());
                    }
                    if (columnIndex == 3) {
                        user.setFirstName(cell.getStringCellValue());
                    }
                    if (columnIndex == 4) {
                        user.setLastName(cell.getStringCellValue());
                    }
                    if (columnIndex > 4) {
                        userMap.put(headers.get(columnIndex), cell.getStringCellValue());
                    }
                }

            });

            System.out.println(user);
            users.add(user);
            String workbookPath = null;
            try {
                workbookPath = createUserFile(userMap, user);
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
            if (user.getEmailAddress() != null) {
                try {
                    mailService.sendEmailWithAttachment(user, workbookPath);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                System.out.println();
            }
        });
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }


    public String createUserFile(Map<String, String> usermap, User user) throws InvalidFormatException {
        String path = null;
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet newsheet = workbook.createSheet("Details");

        int rowNum = 0;
        Row headerRow = newsheet.createRow(rowNum++);
        Set<String> entries = usermap.keySet();
        int headerCellNum = 0;
        for (String h : entries) {
            Cell cell = headerRow.createCell(headerCellNum);
            cell.setCellValue(h);
            headerCellNum++;
        }

        Row rows = newsheet.createRow(rowNum++);
        int x = 0;
        for (Map.Entry<String, String> entry : usermap.entrySet()) {
            System.out.println("key: " + entry.getKey() + "val: " + entry.getValue());
            Cell cell = rows.createCell(x);
            cell.setCellValue(entry.getValue());
            x++;
        }

        path = "C:\\Users\\Diwakar\\Desktop\\workbook.xlsx";
        try (OutputStream fileOut = new FileOutputStream(path)) {
            workbook.write(fileOut);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (user.getPassWord() != null) {
            excelPasswordProtection(path, user);
        }
        return path;

    }


    public void excelPasswordProtection(String path, User user) {
        POIFSFileSystem fs = new POIFSFileSystem();
        EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
        Encryptor enc = info.getEncryptor();
        enc.confirmPassword(user.getPassWord());

        OPCPackage opc = null;
        try {
            opc = OPCPackage.open(new File(path), PackageAccess.READ_WRITE);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        OutputStream os = null;
        try {
            os = enc.getDataStream(fs);
            opc.save(os);
            opc.close();
            FileOutputStream fos = new FileOutputStream(path);
            fs.writeFilesystem(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        System.out.println("File created!!");


    }


//    public String createUserFile(Map<String, String> usermap) {
//        String path = null;
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet newsheet = workbook.createSheet("Details");
//
//        int rowNum = 0;
//        Row headerRow = newsheet.createRow(rowNum++);
//        Set<String> entries = usermap.keySet();
//        int headerCellNum = 0;
//        for (String h : entries) {
//            Cell cell = headerRow.createCell(headerCellNum);
//            cell.setCellValue(h);
//            headerCellNum++;
//        }
//
//        Row rows = newsheet.createRow(rowNum++);
//        int x = 0;
//        for (Map.Entry<String, String> entry : usermap.entrySet()) {
//            System.out.println("key: " + entry.getKey() + "val: " + entry.getValue());
//            Cell cell = rows.createCell(x);
//            cell.setCellValue(entry.getValue());
//            x++;
//        }
//
//        path = "C:\\Users\\Diwakar\\Desktop\\workbook.xls";
//        try (OutputStream fileOut = new FileOutputStream(path)) {
//            workbook.write(fileOut);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return path;
//
//    }

}

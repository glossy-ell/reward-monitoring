package com.example.reward_monitoring.statistics.answerMsnStat.daily.service;

import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class AnswerMsnDailyService {


//
//    public Sheet excelDownload(List<AnswerMsn> list, Workbook wb){
//
//        int size = list.size();
//        Sheet sheet = wb.createSheet("정답 미션 목록");
//        Row row = null;
//        Cell cell = null;
//        CellStyle cellStyle = wb.createCellStyle();
//        applyCellStyle(cellStyle);
//        int rowNum = 0;
//
//        row = sheet.createRow(rowNum++);
//        cell = row.createCell(0);
//        cell.setCellValue("quizIdx");
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(1);
//        cell.setCellValue("기본 수량");
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(2);
//        cell.setCellValue("데일리캡");
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(3);
//        cell.setCellValue("광고주");
//        sheet.setColumnWidth(3, 16 * 256); //8자
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(4);
//        cell.setCellValue("광고주 상세");
//        sheet.setColumnWidth(4, 16 * 256);
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(5);
//        cell.setCellValue("미션 제목");
//        cell.setCellStyle(cellStyle);
//        sheet.setColumnWidth(5, 16 * 256);
//        cell = row.createCell(6);
//        cell.setCellValue("미션 정답");
//        sheet.setColumnWidth(6, 20 * 256); //8자
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(7);
//        cell.setCellValue("미션 시작일시");
//        sheet.setColumnWidth(7, 20 * 256);
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(8);
//        cell.setCellValue("미션 종료일시");
//        sheet.setColumnWidth(8, 20 * 256);
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(9);
//        cell.setCellValue("데일리캡 시작일");
//        sheet.setColumnWidth(9, 20 * 256);
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(10);
//        cell.setCellValue("데일리캡 종료일");
//        sheet.setColumnWidth(10, 20 * 256);
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(11);
//        cell.setCellValue("미션 사용여부");
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(12);
//        cell.setCellValue("미션 노출여부");
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(13);
//        cell.setCellValue("중복참여");
//        cell.setCellStyle(cellStyle);
//        cell = row.createCell(14);
//        cell.setCellValue("재참여 가능일");
//        cell.setCellStyle(cellStyle);
//        sheet.setColumnWidth(14, 20 * 256);
//
//        for (AnswerMsn answerMsn : list) {
//            row = sheet.createRow(rowNum++);
//            cell = row.createCell(0);
//            cell.setCellValue(answerMsn.getIdx());
//            cell.setCellStyle(cellStyle);
//            cell = row.createCell(1);
//            cell.setCellValue(answerMsn.getMissionDefaultQty());
//            cell.setCellStyle(cellStyle);
//            cell = row.createCell(2);
//            cell.setCellValue(answerMsn.getMissionDailyCap());
//            cell.setCellStyle(cellStyle);
//            cell = row.createCell(3);
//            cell.setCellValue(answerMsn.getAdvertiser().getAdvertiser());
//            cell.setCellStyle(cellStyle);
//            cell = row.createCell(4);
//            cell.setCellValue(answerMsn.getAdvertiserDetails());
//            cell.setCellStyle(cellStyle);
//            cell = row.createCell(5);
//            cell.setCellValue(answerMsn.getMissionTitle());
//            cell.setCellStyle(cellStyle);
//            cell = row.createCell(6);
//            cell.setCellValue(answerMsn.getMissionAnswer());
//            cell.setCellStyle(cellStyle);
//
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            cell = row.createCell(7);
//            cell.setCellStyle(cellStyle);
//            cell.setCellValue(answerMsn.getStartAtMsn().format(formatter));
//            cell = row.createCell(8);
//            cell.setCellStyle(cellStyle);
//            cell.setCellValue(answerMsn.getEndAtMsn().format(formatter));
//            cell = row.createCell(9);
//            cell.setCellStyle(cellStyle);
//            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            cell.setCellValue(answerMsn.getStartAtCap().format(formatter_));
//            cell.setCellStyle(cellStyle);
//            cell = row.createCell(10);
//            cell.setCellStyle(cellStyle);
//            cell.setCellValue(answerMsn.getEndAtCap().format(formatter_));
//            cell = row.createCell(11);
//            cell.setCellStyle(cellStyle);
//            if(answerMsn.isMissionActive())
//                cell.setCellValue("활성");
//            else
//                cell.setCellValue("비활성");
//            cell = row.createCell(12);
//            cell.setCellStyle(cellStyle);
//            if(answerMsn.isMissionExposure())
//                cell.setCellValue("노출");
//            else
//                cell.setCellValue("비노출");
//
//            cell = row.createCell(13);
//            cell.setCellStyle(cellStyle);
//            if(answerMsn.isDupParticipation())
//                cell.setCellValue("중복 허용");
//            else
//                cell.setCellValue("중복 불가");
//            cell = row.createCell(14);
//            cell.setCellStyle(cellStyle);
//            cell.setCellValue(answerMsn.getReEngagementDay());
//        }
//        return sheet;
//    }
//
//    private void applyCellStyle(CellStyle cellStyle) {
//        XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle;
//        cellStyle.setAlignment(HorizontalAlignment.CENTER);
//        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        cellStyle.setBorderLeft(BorderStyle.THIN);
//        cellStyle.setBorderTop(BorderStyle.THIN);
//        cellStyle.setBorderRight(BorderStyle.THIN);
//        cellStyle.setBorderBottom(BorderStyle.THIN);
//    }
//
//    public List<AnswerMsn> getAnswerMsnsDaily() {
//    }
}

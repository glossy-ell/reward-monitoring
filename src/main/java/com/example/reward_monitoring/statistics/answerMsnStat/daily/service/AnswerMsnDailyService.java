package com.example.reward_monitoring.statistics.answerMsnStat.daily.service;

import com.example.reward_monitoring.mission.answerMsn.dto.AnswerMsnSearchDto;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.answerMsn.repository.AnswerMsnRepository;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.dto.AnswerMsnDailyStatSearchDto;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.entity.AnswerMsnDailyStat;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.repository.AnswerMsnDailyStatRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AnswerMsnDailyService {

    @Autowired
    private AnswerMsnDailyStatRepository answerMsnDailyStatRepository;





    public Sheet excelDownload(List<AnswerMsnDailyStat> list, Workbook wb){

        int size = list.size();
        Sheet sheet = wb.createSheet("정답 미션 목록");
        Row row = null;
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("참여일");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("매체사 IDX");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("광고주 IDX");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("광고주 디테일");
        sheet.setColumnWidth(3, 16 * 256); //8자
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("광고주 상세");
        sheet.setColumnWidth(4, 16 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("미션 IDX");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(5, 16 * 256);
        cell = row.createCell(6);
        cell.setCellValue("미션 제목");
        sheet.setColumnWidth(6, 20 * 256); //8자
        cell.setCellStyle(cellStyle);
        cell = row.createCell(7);
        cell.setCellValue("랜딩 카운트");
        sheet.setColumnWidth(7, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(8);
        cell.setCellValue("참여 카운트");
        sheet.setColumnWidth(8, 20 * 256);
        cell.setCellStyle(cellStyle);


        for (AnswerMsnDailyStat answerMsnDailyStat: list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(answerMsnDailyStat.getPartDate());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue(answerMsnDailyStat.getMediaCompany().getIdx());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue(answerMsnDailyStat.getAdvertiser().getIdx());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellValue(answerMsnDailyStat.getAnswerMsn().getAdvertiserDetails());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(4);
            cell.setCellValue(answerMsnDailyStat.getAnswerMsn().getIdx());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(answerMsnDailyStat.getAnswerMsn().getMissionTitle());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellValue(answerMsnDailyStat.getLandingCnt());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(7);
            cell.setCellValue(answerMsnDailyStat.getPartCnt());
            cell.setCellStyle(cellStyle);
        }
        return sheet;
    }

    private void applyCellStyle(CellStyle cellStyle) {
        XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle;
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
    }

    public List<AnswerMsnDailyStat> getAnswerMsnsDailys() {
        return answerMsnDailyStatRepository.findAll();
    }

    public List<AnswerMsnDailyStat> searchAnswerMsnDaily(AnswerMsnDailyStatSearchDto dto) {

        List<AnswerMsnDailyStat> result = new ArrayList<>();


        if(dto.getUrl() != null)
            result.addAll(answerMsnDailyStatRepository.findByServer_ServerUrl(dto.getUrl()));

        if(dto.getAdvertiser()!=null)
            result.addAll(answerMsnDailyStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser()));

        if(dto.getMediacompany()!=null)
            result.addAll(answerMsnDailyStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany()));

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    result.addAll(answerMsnDailyStatRepository.findByStartAt(dto.getStartAt()));
                else
                    result.addAll(answerMsnDailyStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt()));

            }
            else
                result.addAll(answerMsnDailyStatRepository.findByEndAt(dto.getEndAt()));
        }

        return result.stream().distinct().collect(Collectors.toList());
    }
}

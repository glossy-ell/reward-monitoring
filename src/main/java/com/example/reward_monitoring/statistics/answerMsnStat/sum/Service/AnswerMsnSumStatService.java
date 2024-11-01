package com.example.reward_monitoring.statistics.answerMsnStat.sum.Service;


import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanyQuizSumSearchDto;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.repository.AnswerMsnDailyStatRepository;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.dto.AnswerMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.answerMsnStat.sum.repository.AnswerMsnSumStatRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnswerMsnSumStatService {

    @Autowired
    private AnswerMsnSumStatRepository answerMsnSumStatRepository;

    @Autowired
    private AnswerMsnDailyStatRepository answerMsnDailyStatRepository;

    public List<AnswerMsnSumStat> getAnswerMsnSumStats(){
        return answerMsnSumStatRepository.findAll();
    }

    public List<AnswerMsnSumStat> getAnswerMsnSumStatsMonth(LocalDate currentTime, LocalDate past){
        return answerMsnSumStatRepository.findMonth(currentTime,past);
    }

    public List<AnswerMsnSumStat> getAnswerMsnSumStatsMonthByAffiliate(LocalDate currentTime, LocalDate past,int aidx){
        return answerMsnSumStatRepository.findMonthByAffiliate(currentTime,past,aidx);
    }


    public List<AnswerMsnSumStat> searchAnswerMsnSum(AnswerMsnSumStatSearchDto dto) {

        List<AnswerMsnSumStat> target_serverUrl = null;
        List<AnswerMsnSumStat> target_advertiser = null;
        List<AnswerMsnSumStat> target_mediaCompany= null;
        List<AnswerMsnSumStat> target_date = null;


        List<AnswerMsnSumStat> result;
        boolean changed = false;

        if(dto.getServerUrl() != null)
            target_serverUrl = answerMsnSumStatRepository.findByServer_ServerUrl(dto.getServerUrl());

        if(dto.getAdvertiser()!=null)
            target_advertiser = answerMsnSumStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser());

        if(dto.getMediacompany()!=null)
            target_mediaCompany = answerMsnSumStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany());

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    target_date = answerMsnSumStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = answerMsnSumStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt());
            }
            else
                target_date = answerMsnSumStatRepository.findByEndAt(dto.getEndAt());
        }
        result = new ArrayList<>(answerMsnSumStatRepository.findAll());

        if(target_serverUrl!= null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(AnswerMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_advertiser!= null) {
            Set<Integer> idxSet = target_advertiser.stream().map(AnswerMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_mediaCompany!= null) {
            Set<Integer> idxSet = target_mediaCompany.stream().map(AnswerMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_date!= null) {
            Set<Integer> idxSet = target_date.stream().map(AnswerMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();
        return result;
    }

    public Sheet excelDownloadCurrent(List<AnswerMsnSumStat> list, Workbook wb,int landSum,int PartSum) {

        int size = list.size();
        Sheet sheet = wb.createSheet("정답 미션 합산 리포트");
        Row row = null;
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(0, 16 * 256); //8자
        cell.setCellValue("참여일");

        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("랜딩 카운트");
        sheet.setColumnWidth(1, 16 * 256); //8자

        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("참여 카운트");
        sheet.setColumnWidth(2, 16 * 256); //8자
        cell.setCellStyle(cellStyle);
        for (AnswerMsnSumStat answerMsnSumStat: list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cell.setCellValue(answerMsnSumStat.getDate().format(formatter_));
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue(answerMsnSumStat.getLandingCnt());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(answerMsnSumStat.getPartCnt());
            cell.setCellStyle(cellStyle);
        }
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("합산");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue(landSum);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue(PartSum);
        cell.setCellStyle(cellStyle);

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



    public List<AnswerMsnSumStat> getAnswerMsnSumStatsByAffiliate(int aidx) {
        return answerMsnSumStatRepository.findByMediaCompanyIdx(aidx);
    }


    public List<AnswerMsnSumStat> searchByAffiliate(List<AnswerMsnSumStat> target, MediaCompanyQuizSumSearchDto dto) {

        List<AnswerMsnSumStat> target_date = null;
        List<AnswerMsnSumStat> result = target;

        boolean changed = false;

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    target_date = answerMsnSumStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = answerMsnSumStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt());
            }
            else
                target_date = answerMsnSumStatRepository.findByEndAt(dto.getEndAt());
        }

        if(target_date!= null) {
            Set<Integer> idxSet = target_date.stream().map(AnswerMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();

        return result;
    }
}

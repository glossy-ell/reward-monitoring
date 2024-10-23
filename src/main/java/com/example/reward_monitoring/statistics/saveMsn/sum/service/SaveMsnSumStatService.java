package com.example.reward_monitoring.statistics.saveMsn.sum.service;



import com.example.reward_monitoring.statistics.answerMsnStat.sum.entity.AnswerMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.dto.SaveMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.sum.entity.SaveMsnSumStat;
import com.example.reward_monitoring.statistics.saveMsn.sum.repository.SaveMsnSumStatRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SaveMsnSumStatService {

    @Autowired
    private SaveMsnSumStatRepository saveMsnSumStatRepository;

    public List<SaveMsnSumStat> getSaveMsnSumStats(){
        return saveMsnSumStatRepository.findAll();
    }

    public List<SaveMsnSumStat> getSaveMsnSumStatsMonth(LocalDate currentTime, LocalDate past){
        return saveMsnSumStatRepository.findMonth(currentTime,past);
    }

    public List<SaveMsnSumStat> searchSaveMsnSum(SaveMsnSumStatSearchDto dto) {



        List<SaveMsnSumStat> target_serverUrl = null;
        List<SaveMsnSumStat> target_advertiser = null;
        List<SaveMsnSumStat> target_mediaCompany= null;
        List<SaveMsnSumStat> target_date = null;


        List<SaveMsnSumStat> result;
        boolean changed = false;

        if(dto.getUrl() != null)
            target_serverUrl = saveMsnSumStatRepository.findByServer_ServerUrl(dto.getUrl());

        if(dto.getAdvertiser()!=null)
            target_advertiser = saveMsnSumStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser());

        if(dto.getMediacompany()!=null)
            target_mediaCompany = saveMsnSumStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany());

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    target_date = saveMsnSumStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = saveMsnSumStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt());
            }
            else
                target_date = saveMsnSumStatRepository.findByEndAt(dto.getEndAt());
        }
        result = new ArrayList<>(saveMsnSumStatRepository.findAll());

        if(target_serverUrl!= null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(SaveMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnSumStat -> idxSet.contains(saveMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_advertiser!= null) {
            Set<Integer> idxSet = target_advertiser.stream().map(SaveMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnSumStat -> idxSet.contains(saveMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_mediaCompany!= null) {
            Set<Integer> idxSet = target_mediaCompany.stream().map(SaveMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnSumStat -> idxSet.contains(saveMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_date!= null) {
            Set<Integer> idxSet = target_date.stream().map(SaveMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnSumStat -> idxSet.contains(saveMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();
        return result;
    }

    public Sheet excelDownloadCurrent(List<SaveMsnSumStat> list, Workbook wb,int landSum,int PartSum) {

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
        sheet.setColumnWidth(3, 16 * 256); //8자
        cell.setCellValue("참여일");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("랜딩 카운트");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("참여 카운트");
        cell.setCellStyle(cellStyle);
        for (SaveMsnSumStat saveMsnSumStat: list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(saveMsnSumStat.getDate());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue(saveMsnSumStat.getLandingCnt());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue(saveMsnSumStat.getPartCnt());
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
}

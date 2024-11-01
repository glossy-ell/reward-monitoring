package com.example.reward_monitoring.statistics.saveMsn.daily.service;



import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanySightseeingDailySearchDto;
import com.example.reward_monitoring.statistics.saveMsn.daily.dto.SaveMsnDailyStatSearchDto;
import com.example.reward_monitoring.statistics.saveMsn.daily.entity.SaveMsnDailyStat;
import com.example.reward_monitoring.statistics.saveMsn.daily.repository.SaveMsnDailyStatRepository;
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
public class SaveMsnDailyService {

    @Autowired
    private SaveMsnDailyStatRepository saveMsnDailyStatRepository;

    public List<SaveMsnDailyStat> getSaveMsnsDailys() {
        return saveMsnDailyStatRepository.findAll();
    }
    public List<SaveMsnDailyStat>  getSaveMsnsDaily(int idx, LocalDate currentTime, LocalDate past) {return saveMsnDailyStatRepository.findByMsnIdx(idx,currentTime,past);}
    public List<SaveMsnDailyStat>  getSaveMsnsDailysMonth(LocalDate currentTime, LocalDate past) {return saveMsnDailyStatRepository.findMonth(currentTime,past);}

    public Sheet excelDownload(List<SaveMsnDailyStat> list, Workbook wb){

        int size = list.size();
        Sheet sheet = wb.createSheet("저장 미션 목록");
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


        for (SaveMsnDailyStat saveMsnDailyStat: list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(saveMsnDailyStat.getPartDate());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue(saveMsnDailyStat.getMediaCompany().getIdx());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue(saveMsnDailyStat.getAdvertiser().getIdx());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellValue(saveMsnDailyStat.getSaveMsn().getAdvertiserDetails());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(4);
            cell.setCellValue(saveMsnDailyStat.getSaveMsn().getIdx());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(saveMsnDailyStat.getSaveMsn().getMissionTitle());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellValue(saveMsnDailyStat.getLandingCnt());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(7);
            cell.setCellValue(saveMsnDailyStat.getPartCnt());
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


    public List<SaveMsnDailyStat> searchSaveMsnDaily(SaveMsnDailyStatSearchDto dto) {

        List<SaveMsnDailyStat>  target_date = null;
        List<SaveMsnDailyStat>  target_serverUrl = null;
        List<SaveMsnDailyStat> target_advertiser = null;
        List<SaveMsnDailyStat>  target_companyName = null;

        List<SaveMsnDailyStat>  result;
        boolean changed = false;

        result = new ArrayList<>(saveMsnDailyStatRepository.findAll());

        if(dto.getUrl() != null)
            target_serverUrl = saveMsnDailyStatRepository.findByServer_ServerUrl(dto.getUrl());

        if(dto.getAdvertiser()!=null)
            target_advertiser = saveMsnDailyStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser());

        if(dto.getMediacompany()!=null)
            target_companyName = saveMsnDailyStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany());

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    target_date = saveMsnDailyStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = saveMsnDailyStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt());

            }
            else
                target_date = saveMsnDailyStatRepository.findByEndAt(dto.getEndAt());
        }

        if(target_date != null){
            Set<Integer> idxSet = target_date.stream().map(SaveMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDailyStat-> idxSet.contains(saveMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_serverUrl != null){
            Set<Integer> idxSet = target_serverUrl.stream().map(SaveMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDailyStat-> idxSet.contains(saveMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_advertiser != null){
            Set<Integer> idxSet = target_advertiser.stream().map(SaveMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDailyStat-> idxSet.contains(saveMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_companyName != null){
            Set<Integer> idxSet = target_companyName.stream().map(SaveMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDailyStat-> idxSet.contains(saveMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();

        return result;
    }

    public List<SaveMsnDailyStat> findByMediaCompany(int aidx){
        return saveMsnDailyStatRepository.findByMediaCompanyIdx(aidx);
    }

    public List<SaveMsnDailyStat> searchSaveMsnDailyByAffiliate(List<SaveMsnDailyStat> target, MediaCompanySightseeingDailySearchDto dto) {

        List<SaveMsnDailyStat> target_date = null;


        List<SaveMsnDailyStat> result = target;
        boolean changed = false;

        result = new ArrayList<>(saveMsnDailyStatRepository.findAll());
        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    target_date = saveMsnDailyStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = saveMsnDailyStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt());
            }
            else
                target_date = saveMsnDailyStatRepository.findByEndAt(dto.getEndAt());
        }

        if(target_date != null){
            Set<Integer> idxSet = target_date.stream().map(SaveMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDailyStat-> idxSet.contains(saveMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();

        return result;
    }
}

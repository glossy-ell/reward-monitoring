package com.example.reward_monitoring.statistics.searchMsn.sum.service;




import com.example.reward_monitoring.statistics.searchMsn.sum.dto.SearchMsnSumStatSearchDto;
import com.example.reward_monitoring.statistics.searchMsn.sum.entity.SearchMsnSumStat;
import com.example.reward_monitoring.statistics.searchMsn.sum.repository.SearchMsnSumStatRepository;
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
public class SearchMsnSumStatService {

    @Autowired
    private SearchMsnSumStatRepository searchMsnSumStatRepository;

    public List<SearchMsnSumStat> getSearchMsnSumStats(){
        return searchMsnSumStatRepository.findAll();
    }

    public List<SearchMsnSumStat> getSearchMsnSumStatsMonth(LocalDate currentTime, LocalDate past){
        return searchMsnSumStatRepository.findMonth(currentTime,past);
    }
    public List<SearchMsnSumStat> searchSearchMsnSum(SearchMsnSumStatSearchDto dto) {
        List<SearchMsnSumStat> target_serverUrl = null;
        List<SearchMsnSumStat> target_advertiser = null;
        List<SearchMsnSumStat> target_mediaCompany= null;
        List<SearchMsnSumStat> target_date = null;


        List<SearchMsnSumStat> result;
        boolean changed = false;

        if(dto.getUrl() != null)
            target_serverUrl = searchMsnSumStatRepository.findByServer_ServerUrl(dto.getUrl());

        if(dto.getAdvertiser()!=null)
            target_advertiser = searchMsnSumStatRepository.findByAdvertiser_Advertiser(dto.getAdvertiser());

        if(dto.getMediacompany()!=null)
            target_mediaCompany = searchMsnSumStatRepository.findByMediaCompany_CompanyName(dto.getMediacompany());

        if(dto.getStartAt() != null || dto.getEndAt() != null){
            if(dto.getStartAt() != null){
                if(dto.getEndAt() == null)
                    target_date = searchMsnSumStatRepository.findByStartAt(dto.getStartAt());
                else
                    target_date = searchMsnSumStatRepository.findByBothAt(dto.getStartAt(),dto.getEndAt());
            }
            else
                target_date = searchMsnSumStatRepository.findByEndAt(dto.getEndAt());
        }
        result = new ArrayList<>(searchMsnSumStatRepository.findAll());

        if(target_serverUrl!= null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(SearchMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_advertiser!= null) {
            Set<Integer> idxSet = target_advertiser.stream().map(SearchMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_mediaCompany!= null) {
            Set<Integer> idxSet = target_mediaCompany.stream().map(SearchMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_date!= null) {
            Set<Integer> idxSet = target_date.stream().map(SearchMsnSumStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnSumStat -> idxSet.contains(answerMsnSumStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();
        return result;
    }

    public Sheet excelDownloadCurrent(List<SearchMsnSumStat> list, Workbook wb, int landSum, int PartSum) {

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
        for (SearchMsnSumStat searchMsnSumStat: list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(searchMsnSumStat.getDate());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue(searchMsnSumStat.getLandingCnt());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue(searchMsnSumStat.getPartCnt());
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

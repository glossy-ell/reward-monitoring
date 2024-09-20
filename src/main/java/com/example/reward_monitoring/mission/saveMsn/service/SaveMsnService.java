package com.example.reward_monitoring.mission.saveMsn.service;



import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.repository.AdvertiserRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.repository.ServerRepository;
import com.example.reward_monitoring.mission.saveMsn.dto.*;
import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import com.example.reward_monitoring.mission.saveMsn.repository.SaveMsnRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SaveMsnService {
    @Autowired
    private SaveMsnRepository saveMsnRepository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private ServerRepository serverRepository;

    public SaveMsn edit(int idx, SaveMsnEditDto dto) {
        SaveMsn saveMsn =saveMsnRepository.findByIdx(idx);
        if(saveMsn==null)
            return null;
        if(dto.getMissionDefaultQty() != null)
            saveMsn.setMissionDefaultQty(dto.getMissionDefaultQty());
        if(dto.getMissionDailyCap() !=null)
            saveMsn.setMissionDailyCap(dto.getMissionDailyCap());
        if(dto.getMissionTitle()!=null)
            saveMsn.setMissionTitle(dto.getMissionTitle());
        if(dto.getSearchKeyword()!=null)
            saveMsn.setSearchKeyword(dto.getSearchKeyword());
        if (dto.getStartAtMsn() != null)
            saveMsn.setStartAtMsn(dto.getStartAtMsn());
        if (dto.getEndAtMsn() != null)
            saveMsn.setEndAtMsn(dto.getEndAtMsn());
        if (dto.getStartAtCap() != null)
            saveMsn.setStartAtCap(dto.getStartAtCap());
        if (dto.getEndAtCap() != null)
            saveMsn.setEndAtCap(dto.getEndAtCap());
        if (dto.getMissionActive() != null) {
            boolean bool = dto.getMissionActive();
            saveMsn.setMissionActive(bool);
        }
        if (dto.getMissionExposure() != null) {
            boolean bool = dto.getMissionExposure();
            saveMsn.setMissionExposure(bool);
        }
        if (dto.getDupParticipation() != null) {
            boolean bool = dto.getDupParticipation();
            saveMsn.setDupParticipation(bool);
        }
        if (dto.getReEngagementDay() != null) {
            saveMsn.setReEngagementDay(dto.getReEngagementDay());
        }
        return saveMsn;
    }

    public SaveMsn add(SaveMsnReadDto dto) {
        Server serverEntity = serverRepository.findByServerUrl_(dto.getUrl());
        Advertiser advertiserEntity = advertiserRepository.findByAdvertiser_(dto.getAdvertiser());
        return dto.toEntity(advertiserEntity,serverEntity);
    }

    public SaveMsn getSaveMsn(int idx) {
        return saveMsnRepository.findByIdx(idx);
    }

    public List<SaveMsn> getSaveMsns() {
        return saveMsnRepository.findAll();
    }

    public SaveMsn delete(int idx) {
        SaveMsn target = saveMsnRepository.findByIdx(idx);
        if(target==null)
            return null;
        saveMsnRepository.delete(target);
        return target;
    }

    public List<SaveMsn> searchSaveMsn(SaveMsnSearchDto dto) {

        List<SaveMsn> target_date;
        List<SaveMsn> target_dailyCap;

        List<SaveMsn> target_dup_Participation;
        List<SaveMsn> target_mission_active;
        List<SaveMsn> target_mission_exposure;
        List<SaveMsn> target_data_Type;

        List<SaveMsn> target_advertiser;
        List<SaveMsn> target_advertiser_details; // 선택 1
        List<SaveMsn> target_mission_title; // 선택 2


        List<SaveMsn> result = new ArrayList<>();


        if(dto.getStartAtMsn() != null || dto.getEndAtMsn() != null){
            if(dto.getStartAtMsn() != null){
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime start_time = dto.getStartAtMsn().atStartOfDay(zoneId);
                if(dto.getEndAtMsn() == null){
                    target_date = saveMsnRepository.findByStartDate(start_time);
                    result.addAll(target_date);
                }else{
                    ZonedDateTime end_time = dto.getEndAtMsn().atStartOfDay(zoneId);
                    target_date = saveMsnRepository.findByBothDate(start_time,end_time);
                    result.addAll(target_date);
                }

            }
            else {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime end_time = dto.getEndAtMsn().atStartOfDay(zoneId);

                target_date = saveMsnRepository.findByEndDate(end_time);
                result.addAll(target_date);
            }

        }

        if(dto.getStartAtCap() != null || dto.getEndAtCap() != null){
            if(dto.getStartAtCap() != null){
                if(dto.getEndAtCap() == null){
                    target_dailyCap = saveMsnRepository.findByStartAtCap(dto.getStartAtCap());
                    result.addAll(target_dailyCap);
                }else{
                    target_dailyCap = saveMsnRepository.findByBothCap(dto.getStartAtCap(),dto.getEndAtCap());
                    result.addAll(target_dailyCap);
                }

            }
            else {
                target_dailyCap = saveMsnRepository.findByEndAtCap(dto.getEndAtCap());
                result.addAll(target_dailyCap);
            }

        }

        if(dto.getMissionActive() != null){
            target_mission_active = saveMsnRepository.findByMissionActive(dto.getMissionActive());
            result.addAll(target_mission_active);
        }

        if(dto.getDupParticipation() != null){
            target_dup_Participation = saveMsnRepository.findByDupParticipation(dto.getDupParticipation());
            result.addAll(target_dup_Participation);
        }
        if(dto.getMissionExposure() != null){
            target_mission_exposure = saveMsnRepository.findByMissionExposure(dto.getMissionExposure());
            result.addAll(target_mission_exposure);
        }

        if(dto.getDataType() != null){
            target_data_Type= saveMsnRepository.findByDataType(dto.getDataType());
            result.addAll(target_data_Type);
        }

        if(dto.getAdvertiser()!=null){
            target_advertiser = saveMsnRepository.findByAdvertiser(dto.getAdvertiser());
            result.addAll(target_advertiser);
        }

        if(dto.getAdvertiserDetails()!=null){
            target_advertiser_details = saveMsnRepository.findByAdvertiserDetails(dto.getAdvertiserDetails());
            result.addAll(target_advertiser_details);
        }

        if(dto.getMissionTitle()!= null) {
            target_mission_title = saveMsnRepository.findByMissionTitle(dto.getMissionTitle());
            result.addAll(target_mission_title);
        }
        return result.stream().distinct().collect(Collectors.toList());

    }

    public Sheet excelDownload(List<SaveMsn> list, Workbook wb){

        int size = list.size();
        Sheet sheet = wb.createSheet("저장 미션 목록");
        Row row = null;
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("quizIdx");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("기본 수량");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("데일리캡");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("광고주");
        sheet.setColumnWidth(3, 16 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("광고주 상세");
        sheet.setColumnWidth(4, 16 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("미션 제목");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(5, 16 * 256);
        cell = row.createCell(6);
        cell.setCellValue("검색 키워드");
        sheet.setColumnWidth(6, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(7);
        cell.setCellValue("미션 시작일시");
        sheet.setColumnWidth(7, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(8);
        cell.setCellValue("미션 종료일시");
        sheet.setColumnWidth(8, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(9);
        cell.setCellValue("데일리캡 시작일");
        sheet.setColumnWidth(9, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(10);
        cell.setCellValue("데일리캡 종료일");
        sheet.setColumnWidth(10, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(11);
        cell.setCellValue("미션 사용여부");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(12);
        cell.setCellValue("미션 노출여부");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(13);
        cell.setCellValue("중복참여");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(14);
        cell.setCellValue("재참여 가능일");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(14, 20 * 256);

        for (SaveMsn saveMsn : list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(saveMsn.getIdx());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue(saveMsn.getMissionDefaultQty());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue(saveMsn.getMissionDailyCap());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellValue(saveMsn.getAdvertiser().getAdvertiser());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(4);
            cell.setCellValue(saveMsn.getAdvertiserDetails());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(saveMsn.getMissionTitle());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellValue(saveMsn.getSearchKeyword());
            cell.setCellStyle(cellStyle);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(saveMsn.getStartAtMsn().format(formatter));
            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(saveMsn.getEndAtMsn().format(formatter));
            cell = row.createCell(9);
            cell.setCellStyle(cellStyle);
            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cell.setCellValue(saveMsn.getStartAtCap().format(formatter_));
            cell.setCellStyle(cellStyle);
            cell = row.createCell(10);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(saveMsn.getEndAtCap().format(formatter_));
            cell = row.createCell(11);
            cell.setCellStyle(cellStyle);
            if(saveMsn.isMissionActive())
                cell.setCellValue("활성");
            else
                cell.setCellValue("비활성");
            cell = row.createCell(12);
            cell.setCellStyle(cellStyle);
            if(saveMsn.isMissionExposure())
                cell.setCellValue("노출");
            else
                cell.setCellValue("비노출");

            cell = row.createCell(13);
            cell.setCellStyle(cellStyle);
            if(saveMsn.isDupParticipation())
                cell.setCellValue("중복 허용");
            else
                cell.setCellValue("중복 불가");
            cell = row.createCell(14);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(saveMsn.getReEngagementDay());
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

    @Transactional
    public boolean readExcel(MultipartFile file)throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        SaveMsnReadDto dto = new SaveMsnReadDto();
        Advertiser advertiserEntity=null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter_date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {

            XSSFRow row = worksheet.getRow(i);

            if (row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC) {
                dto.setMissionDefaultQty((int) row.getCell(1).getNumericCellValue());
            }
            if(row.getCell(2)!=null)
                dto.setMissionDailyCap((int)row.getCell(2).getNumericCellValue());

            advertiserEntity = advertiserRepository.findByAdvertiser_(row.getCell(3).getStringCellValue());
            //셀에있는 데이터를 읽어와 그걸로 repository 에서 일치하는 advertiser 를 가져온다.
            if(row.getCell(4)!=null)
                dto.setAdvertiserDetails(row.getCell(4).getStringCellValue());
            if(row.getCell(5)!=null)
                dto.setMissionTitle(row.getCell(5).getStringCellValue());
            if(row.getCell(6)!=null)
                dto.setSearchKeyword(row.getCell(6).getStringCellValue());
            if(row.getCell(7)!=null)
                dto.setStartAtMsn(ZonedDateTime.of(LocalDateTime.parse(row.getCell(7).getStringCellValue(),formatter),ZoneId.systemDefault()));
            if(row.getCell(8)!=null)
                dto.setEndAtMsn(ZonedDateTime.of(LocalDateTime.parse(row.getCell(8).getStringCellValue(),formatter),ZoneId.systemDefault()));
            if(row.getCell(9)!=null)
            dto.setStartAtCap(LocalDate.parse(row.getCell(9).getStringCellValue(),formatter_date));
            if(row.getCell(10)!=null)
                dto.setEndAtCap(LocalDate.parse(row.getCell(10).getStringCellValue(),formatter_date));
            if(Objects.equals(row.getCell(11).getStringCellValue(), "활성"))
                dto.setMissionActive(true);
            else
                dto.setMissionActive(false);
            if(Objects.equals(row.getCell(12).getStringCellValue(), "노출"))
                dto.setMissionExposure(true);
            else
                dto.setMissionExposure(false);
            if(Objects.equals(row.getCell(13).getStringCellValue(),"중복 허용"))
                dto.setDupParticipation(true);
            else
                dto.setDupParticipation(false);
            dto.setReEngagementDay((int)row.getCell(14).getNumericCellValue());

            saveMsnRepository.save(dto.toEntity(advertiserEntity,null));

        }
        return true;


    }
    public boolean allMissionEnd() {

        List<SaveMsn> target = getSaveMsns();
        if(target == null)
            return false;

        for(SaveMsn saveMsn : target){
            saveMsn.setMissionExposure(false);
            saveMsn.setMissionActive(false);
            saveMsnRepository.save(saveMsn);
        }
        return true;
    }
    public List<SaveMsn> searchSaveMsnByConsumed(SaveMsnSearchDto dto) {
        return null;
    }

    public boolean changeAbleDay(SaveMsnAbleDayDto dto, int idx) {
        SaveMsn saveMsn = saveMsnRepository.findByIdx(idx);
        if(saveMsn == null)
            return false;

        saveMsn.setDupParticipation(dto.isDupParticipation());
        if(dto.getReEngagementDay()!=null)
            saveMsn.setReEngagementDay(dto.getReEngagementDay());
        return true;

    }

    public boolean changeMissionActive(int idx, SaveMsnActiveDto dto) {
        SaveMsn target =saveMsnRepository.findByIdx(idx);
        if(target ==null)
            return false;
        target.setMissionActive(dto.isActive());
        return true;
    }
    public boolean changeMissionExpose(int idx, SaveMsnExposeDto dto) {

        SaveMsn target =saveMsnRepository.findByIdx(idx);
        if(target ==null)
            return false;
        target.setMissionExposure(dto.isExpose());
        return true;
    }
}

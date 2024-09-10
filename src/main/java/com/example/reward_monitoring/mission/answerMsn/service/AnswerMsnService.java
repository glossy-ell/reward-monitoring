package com.example.reward_monitoring.mission.answerMsn.service;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.repository.AdvertiserRepository;
import com.example.reward_monitoring.general.member.dto.MemberReadDto;
import com.example.reward_monitoring.mission.answerMsn.dto.AnswerMsnEditDto;
import com.example.reward_monitoring.mission.answerMsn.dto.AnswerMsnReadDto;
import com.example.reward_monitoring.mission.answerMsn.dto.AnswerMsnSearchDto;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.answerMsn.repository.AnswerMsnRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
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

@Service
public class AnswerMsnService {

    @Autowired
    private AnswerMsnRepository answerMsnRepository;
    @Autowired
    private AdvertiserRepository advertiserRepository;

    public AnswerMsn edit(int idx, AnswerMsnEditDto dto) {
        AnswerMsn answerMsn =answerMsnRepository.findByIdx(idx);
        if(answerMsn==null)
            return null;
        if(dto.getMissionDefaultQty() != null)
            answerMsn.setMissionDefaultQty(dto.getMissionDefaultQty());
        if(dto.getMissionDailyCap() !=null)
            answerMsn.setMissionDailyCap(dto.getMissionDailyCap());
        if(dto.getMissionTitle()!=null)
            answerMsn.setMissionTitle(dto.getMissionTitle());
        if(dto.getMissionAnswer()!=null)
            answerMsn.setMissionAnswer(dto.getMissionAnswer());
        if (dto.getStartAtMsn() != null)
            answerMsn.setStartAtMsn(dto.getStartAtMsn());
        if (dto.getEndAtMsn() != null)
            answerMsn.setEndAtMsn(dto.getEndAtMsn());
        if (dto.getStartAtCap() != null)
            answerMsn.setStartAtCap(dto.getStartAtCap());
        if (dto.getEndAtCap() != null)
            answerMsn.setEndAtCap(dto.getEndAtCap());
        if (dto.getMissionActive() != null) {
            boolean bool = dto.getMissionActive();
            answerMsn.setMissionActive(bool);
        }
        if (dto.getMissionExposure() != null) {
            boolean bool = dto.getMissionExposure();
            answerMsn.setMissionExposure(bool);
        }
        if (dto.getDupParticipation() != null) {
            boolean bool = dto.getDupParticipation();
            answerMsn.setDupParticipation(bool);
        }
        if (dto.getReEngagementDay() != null) {
            answerMsn.setReEngagementDay(dto.getReEngagementDay());
        }
        return answerMsn;
    }

    public AnswerMsn add(AnswerMsnReadDto dto) {
        Advertiser advertiserEntity = advertiserRepository.findByAdvertiser_(dto.getAdvertiser());
        return dto.toEntity(advertiserEntity);
    }

    public AnswerMsn getAnswerMsn(int idx) {
        return answerMsnRepository.findByIdx(idx);
    }

    public List<AnswerMsn> getAnswerMsns() {
        return answerMsnRepository.findAll();
    }

    public AnswerMsn delete(int idx) {
        AnswerMsn target = answerMsnRepository.findByIdx(idx);
        if(target==null)
            return null;
        answerMsnRepository.delete(target);
        return target;
    }


    public List<AnswerMsn> searchAnswerMsn(AnswerMsnSearchDto dto) {

        List<AnswerMsn> target_date=null;
        List<AnswerMsn> target_dailyCap = null;

        List<AnswerMsn> target_dup_Participation = null;
        List<AnswerMsn> target_mission_active = null;
        List<AnswerMsn> target_mission_exposure = null;
        List<AnswerMsn> target_data_Type = null;

        List<AnswerMsn> target_advertiser=null;
        List<AnswerMsn> target_advertiser_details=null; // 선택 1
        List<AnswerMsn> target_mission_title = null; // 선택 2


        List<AnswerMsn> result=null;


        if(dto.getStartAtMsn() != null || dto.getEndAtMsn() != null){
            if(dto.getStartAtMsn() != null){
                if(dto.getEndAtMsn() == null){
                    ZoneId zoneId = ZoneId.of("Asia/Seoul");
                    ZonedDateTime start_time = dto.getStartAtMsn().atStartOfDay(zoneId);
                    target_date = answerMsnRepository.findByStartDate(start_time);
                }else{
                    ZoneId zoneId = ZoneId.of("Asia/Seoul");
                    ZonedDateTime start_time = dto.getStartAtMsn().atStartOfDay(zoneId);
                    ZonedDateTime end_time = dto.getEndAtMsn().atStartOfDay(zoneId);

                    target_date = answerMsnRepository.findByBothDate(start_time,end_time);
                }

            }
            else {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime end_time = dto.getEndAtMsn().atStartOfDay(zoneId);

                target_date = answerMsnRepository.findByEndDate(end_time);
            }

        }

        if(dto.getStartAtCap() != null || dto.getEndAtCap() != null){
            if(dto.getStartAtCap() != null){
                if(dto.getEndAtCap() == null){
                    target_dailyCap = answerMsnRepository.findByStartAtCap(dto.getStartAtCap());
                }else{
                    target_dailyCap = answerMsnRepository.findByBothCap(dto.getStartAtCap(),dto.getEndAtCap());
                }

            }
            else {
                target_dailyCap = answerMsnRepository.findByEndAtCap(dto.getEndAtCap());
            }

        }

        if(dto.getMissionActive() != null){
            target_mission_active = answerMsnRepository.findByMissionActive(dto.getMissionActive());
        }

        if(dto.getDupParticipation() != null){
            target_dup_Participation = answerMsnRepository.findByDupParticipation(dto.getDupParticipation());
        }
        if(dto.getMissionExposure() != null){
            target_mission_exposure = answerMsnRepository.findByMissionExposure(dto.getMissionExposure());
        }

        if(dto.getDataType() != null){
            target_data_Type= answerMsnRepository.findByDataType(dto.getDataType());
        }

        if(dto.getAdvertiser()!=null){
            target_advertiser = answerMsnRepository.findByAdvertiser(dto.getAdvertiser());
        }

        if(dto.getAdvertiserDetails()!=null){
            target_advertiser_details = answerMsnRepository.findByAdvertiserDetails(dto.getAdvertiserDetails());
        }

        if(dto.getMissionTitle()!= null) {
            target_mission_title = answerMsnRepository.findByMissionTitle(dto.getMissionTitle());
        }


        if(target_date!=null) {
            result = new ArrayList<>(target_date);
            if(target_dailyCap != null)
                result.retainAll(target_dailyCap);

            if(target_dup_Participation !=null)
                result.retainAll(target_dup_Participation);
            if(target_mission_active != null)
                result.retainAll(target_mission_active);
            if(target_mission_exposure != null)
                result.retainAll(target_mission_exposure);
            if(target_data_Type != null)
                result.retainAll(target_data_Type);

            if(target_advertiser != null)
                result.retainAll(target_advertiser);
            if(target_advertiser_details != null)
                result.retainAll(target_advertiser_details);
            if(target_mission_title != null)
                result.retainAll(target_mission_title);
        }


        else if(target_dailyCap !=null) {
            result = new ArrayList<>(target_dailyCap);

            if(target_dup_Participation !=null)
                result.retainAll(target_dup_Participation);
            if(target_mission_active != null)
                result.retainAll(target_mission_active);
            if(target_mission_exposure != null)
                result.retainAll(target_mission_exposure);
            if(target_data_Type != null)
                result.retainAll(target_data_Type);
            if(target_advertiser != null)
                result.retainAll(target_advertiser);
            if(target_advertiser_details != null)
                result.retainAll(target_advertiser_details);
            if(target_mission_title != null)
                result.retainAll(target_mission_title);

        }

        else if(target_dup_Participation !=null) {
            result = new ArrayList<>(target_dup_Participation);

            if(target_mission_active != null)
                result.retainAll(target_mission_active);
            if(target_mission_exposure != null)
                result.retainAll(target_mission_exposure);
            if(target_data_Type != null)
                result.retainAll(target_data_Type);
            if(target_advertiser != null)
                result.retainAll(target_advertiser);
            if(target_advertiser_details != null)
                result.retainAll(target_advertiser_details);
            if(target_mission_title != null)
                result.retainAll(target_mission_title);
        }
        else if(target_mission_active !=null) {
            result = new ArrayList<>(target_mission_active);

            if(target_mission_exposure != null)
                result.retainAll(target_mission_exposure);
            if(target_data_Type != null)
                result.retainAll(target_data_Type);
            if(target_advertiser != null)
                result.retainAll(target_advertiser);
            if(target_advertiser_details != null)
                result.retainAll(target_advertiser_details);
            if(target_mission_title != null)
                result.retainAll(target_mission_title);
        }
        else if(target_mission_exposure !=null) {
            result = new ArrayList<>(target_mission_exposure);

            if(target_data_Type != null)
                result.retainAll(target_data_Type);
            if(target_advertiser != null)
                result.retainAll(target_advertiser);
            if(target_advertiser_details != null)
                result.retainAll(target_advertiser_details);
            if(target_mission_title != null)
                result.retainAll(target_mission_title);
        }
        else if(target_data_Type !=null) {
            result = new ArrayList<>(target_data_Type);

            if(target_advertiser != null)
                result.retainAll(target_advertiser);
            if(target_advertiser_details != null)
                result.retainAll(target_advertiser_details);
            if(target_mission_title != null)
                result.retainAll(target_mission_title);
        }
        else if(target_advertiser !=null) {
            result = new ArrayList<>(target_advertiser);

            if(target_advertiser_details != null)
                result.retainAll(target_advertiser_details);
            if(target_mission_title != null)
                result.retainAll(target_mission_title);
        }
        else if (target_advertiser_details != null)
            result = new ArrayList<>(target_advertiser_details);
        else if (target_mission_title != null)
            result = new ArrayList<>(target_mission_title);



        return result;

    }

    public Sheet excelDownload( List<AnswerMsn> list,Workbook wb){

        int size = list.size();
        Sheet sheet = wb.createSheet("정답 미션 목록");
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
        sheet.setColumnWidth(3, 16 * 256); //8자
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
        cell.setCellValue("미션 정답");
        sheet.setColumnWidth(6, 20 * 256); //8자
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

        for (AnswerMsn answerMsn : list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(answerMsn.getIdx());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue(answerMsn.getMissionDefaultQty());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue(answerMsn.getMissionDailyCap());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellValue(answerMsn.getAdvertiser().getAdvertiser());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(4);
            cell.setCellValue(answerMsn.getAdvertiserDetails());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(answerMsn.getMissionTitle());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellValue(answerMsn.getMissionAnswer());
            cell.setCellStyle(cellStyle);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getStartAtMsn().format(formatter));
            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getEndAtMsn().format(formatter));
            cell = row.createCell(9);
            cell.setCellStyle(cellStyle);
            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cell.setCellValue(answerMsn.getStartAtCap().format(formatter_));
            cell.setCellStyle(cellStyle);
            cell = row.createCell(10);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getEndAtCap().format(formatter_));
            cell = row.createCell(11);
            cell.setCellStyle(cellStyle);
            if(answerMsn.isMissionActive())
                cell.setCellValue("활성");
            else
                cell.setCellValue("비활성");
            cell = row.createCell(12);
            cell.setCellStyle(cellStyle);
            if(answerMsn.isMissionExposure())
                cell.setCellValue("노출");
            else
                cell.setCellValue("비노출");
                    
            cell = row.createCell(13);
            cell.setCellStyle(cellStyle);
            if(answerMsn.isDupParticipation())
                cell.setCellValue("중복 허용");
            else
                cell.setCellValue("중복 불가");
            cell = row.createCell(14);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getReEngagementDay());
        }
        return sheet;
    }

    private void applyCellStyle(CellStyle cellStyle) {
        XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle;
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
    }
    public boolean readExcel(MultipartFile file)throws IOException{



        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        AnswerMsnReadDto dto = new AnswerMsnReadDto();
        Advertiser advertiserEntity=null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter_date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {



            XSSFRow row = worksheet.getRow(i);

            if (row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC) {
                dto.setMissionDefaultQty((int) row.getCell(1).getNumericCellValue());
            }
            dto.setMissionDailyCap((int)row.getCell(2).getNumericCellValue());
            advertiserEntity = advertiserRepository.findByAdvertiser_(row.getCell(3).getStringCellValue());
            //셀에있는 데이터를 읽어와 그걸로 repository 에서 일치하는 advertiser 를 가져온다.
            dto.setMissionTitle(row.getCell(4).getStringCellValue());
            dto.setMissionTitle(row.getCell(5).getStringCellValue());
            dto.setMissionAnswer(row.getCell(6).getStringCellValue());
            dto.setStartAtMsn(ZonedDateTime.of(LocalDateTime.parse(row.getCell(7).getStringCellValue(),formatter),ZoneId.systemDefault()));
            dto.setEndAtMsn(ZonedDateTime.of(LocalDateTime.parse(row.getCell(8).getStringCellValue(),formatter),ZoneId.systemDefault()));
            dto.setStartAtCap(LocalDate.parse(row.getCell(9).getStringCellValue(),formatter_date));
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

            answerMsnRepository.save(dto.toEntity(advertiserEntity));

        }
        return true;
    }
}





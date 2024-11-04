package com.example.reward_monitoring.mission.saveMsn.service;



import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.repository.AdvertiserRepository;
import com.example.reward_monitoring.general.mediaCompany.dto.MediaCompanySightseeingCurrentSearchDto;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.repository.ServerRepository;
import com.example.reward_monitoring.mission.saveMsn.dto.*;
import com.example.reward_monitoring.mission.saveMsn.entity.SaveMsn;
import com.example.reward_monitoring.mission.saveMsn.repository.SaveMsnRepository;
import com.example.reward_monitoring.statistics.saveMsn.daily.entity.SaveMsnDailyStat;
import com.example.reward_monitoring.statistics.saveMsn.daily.repository.SaveMsnDailyStatRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SaveMsnService {
    private static final LocalDate TEMP_DATE = LocalDate.of(2000, 1, 1); // 예: 2000-01-01
    private static final LocalDateTime TEMP_ZONED_DATE_TIME = TEMP_DATE.atStartOfDay();

    @Autowired
    private SaveMsnRepository saveMsnRepository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private SaveMsnDailyStatRepository saveMsnDailyStatRepository;
    @Autowired
    private ServerRepository serverRepository;

    public SaveMsn edit(int idx, SaveMsnEditDto dto) {
        SaveMsn saveMsn =saveMsnRepository.findByIdx(idx);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
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

        if (dto.getStartAtMsnDate() != null && dto.getStartTime() != null) {
            LocalDate date = LocalDate.parse(dto.getStartAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getStartTime(), timeFormatter);
            dto.setStartAtMsn(LocalDateTime.of(date,time));
            saveMsn.setStartAtMsn(dto.getStartAtMsn());
        }

        if (dto.getStartAtMsnDate() != null && dto.getStartTime() != null) {
            LocalDate date = LocalDate.parse(dto.getEndAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getEndTime(), timeFormatter);
            dto.setEndAtMsn(LocalDateTime.of(date,time));
            saveMsn.setEndAtMsn(dto.getEndAtMsn());
        }
        if(dto.getMissionContent()!=null && !(dto.getMissionContent().isEmpty()))
            saveMsn.setMissionContent(dto.getMissionContent());

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
            if(!saveMsn.isDupParticipation())
                saveMsn.setReEngagementDay(null);
        }


        if(dto.getMsnUrl()!=null)
            saveMsn.setMsnUrl(dto.getMsnUrl());


        if (dto.getReEngagementDay() != null) {
            saveMsn.setReEngagementDay(dto.getReEngagementDay());
        }

        if(dto.getImageName()!=null && !(dto.getImageName().isEmpty())){
            saveMsn.setImageName(dto.getImageName());
        }

        return saveMsn;
    }

    public SaveMsn add(SaveMsnReadDto dto) {
        Server serverEntity = null;
        Advertiser advertiserEntity = advertiserRepository.findByAdvertiser_(dto.getAdvertiser());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

        if (dto.getStartAtMsnDate() != null && dto.getStartTime() != null) {

            LocalDate date = LocalDate.parse(dto.getStartAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getStartTime(), timeFormatter);
            dto.setStartAtMsn(LocalDateTime.of(date,time));
        }
        if (dto.getEndAtMsnDate() != null && dto.getEndTime() != null) {

            LocalDate date = LocalDate.parse(dto.getEndAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getEndTime(), timeFormatter);
            dto.setEndAtMsn(LocalDateTime.of(date,time));
        }
        if(dto.getUrl()!= null)
            serverEntity = serverRepository.findByServerUrl_(dto.getUrl());
        else
            serverEntity = serverRepository.findByServerUrl_("https://ocb.srk.co.kr");  //default 서버 주소
        dto.setDataType(true);
        if(!dto.getDupParticipation())
            dto.setReEngagementDay(null);

        return dto.toEntity(advertiserEntity,serverEntity);
    }

    public SaveMsn getSaveMsn(int idx) {
        return saveMsnRepository.findByIdx(idx);
    }

    public List<SaveMsn> getSaveMsns() {

        return saveMsnRepository.findAllMission();
    }

    public SaveMsn delete(int idx) {
        SaveMsn target = saveMsnRepository.findByIdx(idx);
        if(target==null)
            return null;
        saveMsnRepository.delete(target);
        return target;
    }

    //검색 조건에 맞는 저장 미션을 검색
    public List<SaveMsn> searchSaveMsn(SaveMsnSearchDto dto) {

        List<SaveMsn> target_date = null;
        List<SaveMsn> target_dailyCap = null;

        List<SaveMsn> target_dup_Participation = null;
        List<SaveMsn> target_mission_active = null;
        List<SaveMsn> target_mission_exposure = null;
        List<SaveMsn> target_data_Type = null;

        List<SaveMsn> target_advertiser = null;
        List<SaveMsn> target_advertiser_details = null; // 선택 1
        List<SaveMsn> target_mission_title = null; // 선택 2


        List<SaveMsn> result;
        boolean changed = false;  //false = 검색결과 없음 , true = 검색결과 있음

        if(dto.getStartAtMsn() != null || dto.getEndAtMsn() != null){
            if(dto.getStartAtMsn() != null){

                LocalDateTime start_time = dto.getStartAtMsn().atStartOfDay();
                if(dto.getEndAtMsn() == null){
                    target_date = saveMsnRepository.findByStartDate(start_time);
                }else{
                    LocalDateTime end_time = dto.getEndAtMsn().atTime(23,59);
                    target_date = saveMsnRepository.findByBothDate(start_time,end_time);
                }
            }
            else {
                LocalDateTime end_time = dto.getEndAtMsn().atTime(23,59);
                target_date = saveMsnRepository.findByEndDate(end_time);
            }

        }

        if(dto.getStartAtCap() != null || dto.getEndAtCap() != null){
            if(dto.getStartAtCap() != null){
                if(dto.getEndAtCap() == null){
                    target_dailyCap = saveMsnRepository.findByStartAtCap(dto.getStartAtCap());
                }else{
                    target_dailyCap = saveMsnRepository.findByBothCap(dto.getStartAtCap(),dto.getEndAtCap());
                }
            }
            else {
                target_dailyCap = saveMsnRepository.findByEndAtCap(dto.getEndAtCap());
            }
        }

        if(dto.getMissionActive() != null)
            target_mission_active = saveMsnRepository.findByMissionActive(dto.getMissionActive());

        if(dto.getDupParticipation() != null)
            target_dup_Participation = saveMsnRepository.findByDupParticipation(dto.getDupParticipation());

        if(dto.getMissionExposure() != null)
            target_mission_exposure = saveMsnRepository.findByMissionExposure(dto.getMissionExposure());

        if(dto.getDataType() != null)
            target_data_Type= saveMsnRepository.findByDataType(dto.getDataType());

        if(dto.getAdvertiser()!=null)
            target_advertiser = saveMsnRepository.findByAdvertiser(dto.getAdvertiser());

        if(dto.getAdvertiserDetails()!=null)
            target_advertiser_details = saveMsnRepository.findByAdvertiserDetails(dto.getAdvertiserDetails());

        if(dto.getMissionTitle()!= null)
            target_mission_title = saveMsnRepository.findByMissionTitle(dto.getMissionTitle());

        result = new ArrayList<>(saveMsnRepository.findAll());

        if(target_date != null){
            Set<Integer> idxSet = target_date.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_dailyCap !=null) {
            Set<Integer> idxSet = target_dailyCap.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_dup_Participation!=null){
            Set<Integer> idxSet = target_dup_Participation.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }else{
            target_dup_Participation = saveMsnRepository.findAll();
            Set<Integer> idxSet = target_dup_Participation.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_mission_active != null) {
            Set<Integer> idxSet = target_mission_active.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn-> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }else{
            target_mission_active = saveMsnRepository.findAll();
            Set<Integer> idxSet = target_mission_active.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_mission_exposure != null) {
            Set<Integer> idxSet = target_mission_exposure.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }else{
            target_mission_exposure = saveMsnRepository.findAll();
            Set<Integer> idxSet = target_mission_exposure.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_data_Type != null) {
            Set<Integer> idxSet = target_data_Type.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser != null) {
            Set<Integer> idxSet = target_advertiser.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }else{
            target_advertiser = saveMsnRepository.findAll();
            Set<Integer> idxSet = target_advertiser.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }



        if(target_advertiser_details != null) {
            Set<Integer> idxSet = target_advertiser_details.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        else if(target_mission_title !=null) {
            Set<Integer> idxSet = target_mission_title.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(!changed)
            result = new ArrayList<>();
        return result;
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
            if(saveMsn.getReEngagementDay()!=null)
                cell.setCellValue(saveMsn.getReEngagementDay());
            else
                cell.setCellValue("-");
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

        Workbook workbook;

        DecimalFormat df = new DecimalFormat("0");
        String fileName = file.getOriginalFilename();

        // 확장자에 따라 Workbook 결정
        try (InputStream is = file.getInputStream()) {
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(is); // For OOXML (.xlsx)
            } else if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(is); // For OLE2 (.xls)
            } else {
                throw new IllegalArgumentException("지원되지 않는 파일 형식입니다: " + fileName);
            }
        }
        Sheet worksheet = workbook.getSheetAt(0);
        SaveMsnReadDto dto = new SaveMsnReadDto();
        Advertiser advertiserEntity = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter_date = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for(int i = worksheet.getPhysicalNumberOfRows() - 1; i >= 1; i--) {

            Row row = worksheet.getRow(i);

            Cell cell = row.getCell(1);
            switch (cell.getCellType()) {
                case NUMERIC:
                    dto.setMissionDefaultQty((int) cell.getNumericCellValue());
                    break;
                case STRING:
                    try {
                        // 텍스트 형식의 숫자를 정수로 변환
                        String cellValue = cell.getStringCellValue();
                        dto.setMissionDefaultQty(Integer.parseInt(cellValue));
                    } catch (NumberFormatException e) {
                        // 변환 실패 시의 처리 (예: 로그 기록, 기본값 설정 등)
                        dto.setMissionDefaultQty(0); // 기본값 설정
                    }
                    break;
                case BLANK:
                    // 빈 셀인 경우 처리 (예: 기본값 설정 등)
                    dto.setMissionDefaultQty(0); // 기본값 설정
                    break;
                default:
                    // 숫자형식도 아니고 문자열도 아닌 경우
                    System.err.println("지원하지 않는 셀 타입: " + cell.getCellType());
                    dto.setMissionDefaultQty(0); // 기본값 설정
                    break;
            }

            cell = row.getCell(2);
            switch (cell.getCellType()) {
                case NUMERIC:
                    dto.setMissionDailyCap((int) cell.getNumericCellValue());
                    break;
                case STRING:
                    try {
                        // 텍스트 형식의 숫자를 정수로 변환
                        String cellValue = cell.getStringCellValue();
                        dto.setMissionDailyCap(Integer.parseInt(cellValue));
                    } catch (NumberFormatException e) {
                        // 변환 실패 시의 처리 (예: 로그 기록, 기본값 설정 등)
                        dto.setMissionDefaultQty(0); // 기본값 설정
                    }
                    break;
                case BLANK:
                    // 빈 셀인 경우 처리 (예: 기본값 설정 등)
                    dto.setMissionDailyCap(0); // 기본값 설정
                    break;
                default:
                    // 숫자형식도 아니고 문자열도 아닌 경우
                    System.err.println("지원하지 않는 셀 타입: " + cell.getCellType());
                    dto.setMissionDailyCap(0); // 기본값 설정
                    break;
            }


            advertiserEntity = advertiserRepository.findByAdvertiser_(row.getCell(3).getStringCellValue());
            //셀에있는 데이터를 읽어와 그걸로 repository 에서 일치하는 advertiser 를 가져온다.
            if(row.getCell(4)!=null)
                dto.setAdvertiserDetails(row.getCell(4).getStringCellValue());
            if(row.getCell(5)!=null)
                dto.setMissionTitle(row.getCell(5).getStringCellValue());
            if(row.getCell(6)!=null)
                dto.setSearchKeyword(row.getCell(6).getStringCellValue());

            if (row.getCell(7) != null) {

                String startAtMsnValue = row.getCell(7).getStringCellValue();
                try {
                    dto.setStartAtMsn(LocalDateTime.parse(startAtMsnValue, formatter));
                } catch (DateTimeException e) {
                    dto.setStartAtMsn(TEMP_ZONED_DATE_TIME); // 임시 날짜 설정
                }
            }
            if (row.getCell(8) != null) {
                String endAtMsnValue = row.getCell(8).getStringCellValue();
                try {
                    dto.setEndAtMsn(LocalDateTime.parse(endAtMsnValue, formatter));
                } catch (DateTimeException e) {
                    dto.setEndAtMsn(TEMP_ZONED_DATE_TIME); // 임시 날짜 설정
                }
            }

            if (row.getCell(9) != null) {
                String startAtCapValue = row.getCell(9).getStringCellValue();
                try {
                        dto.setStartAtCap(LocalDate.parse(startAtCapValue, formatter_date));
                } catch (DateTimeException e) {
                    dto.setStartAtCap(TEMP_DATE); // 임시 날짜 설정
                }
            }
            if (row.getCell(10) != null) {
                String endAtCapValue = row.getCell(10).getStringCellValue();
                try {
                        dto.setEndAtCap(LocalDate.parse(endAtCapValue, formatter_date));
                } catch (DateTimeException e) {
                    dto.setEndAtCap(TEMP_DATE); // 임시 날짜 설정
                }
            }
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
            cell = row.getCell(14);
            switch (cell.getCellType()) {
                case NUMERIC:
                    dto.setMissionDefaultQty((int) cell.getNumericCellValue());
                    break;
                case STRING:
                    try {
                        // 텍스트 형식의 숫자를 정수로 변환
                        String cellValue = cell.getStringCellValue();
                        dto.setMissionDefaultQty(Integer.parseInt(cellValue));
                    } catch (NumberFormatException e) {
                        dto.setMissionDefaultQty(0); // 기본값 설정
                    }
                    break;
                case BLANK:
                    // 빈 셀인 경우 처리 (예: 기본값 설정 등)
                    dto.setMissionDefaultQty(0); // 기본값 설정
                    break;
                default:
                    // 숫자형식도 아니고 문자열도 아닌 경우
                    System.err.println("지원하지 않는 셀 타입: " + cell.getCellType());
                    dto.setMissionDefaultQty(0); // 기본값 설정
                    break;
            }
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

    public boolean changeMissionExpose(int idx, SaveMsnExposeDto dto) {

        SaveMsn target =saveMsnRepository.findByIdx(idx);
        if(target ==null)
            return false;
        target.setMissionExposure(dto.isExpose());
        return true;
    }

    public boolean hidden(int idx) {

        SaveMsn target= saveMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setDataType(false);
        saveMsnRepository.save(target);
        return true;
    }

    public boolean setOffMissionIsUsed(int idx) {
        List<SaveMsn> saveMsns = getSaveMsns();
        Collections.reverse(saveMsns);

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (idx - 1) * limit;


        // 전체 리스트의 크기 체크
        List<SaveMsn> limitedSaveMsns;
        if (startIndex < saveMsns.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsns.size());
            limitedSaveMsns = saveMsns.subList(startIndex, endIndex);
        } else {
            return false;
        }
        for (SaveMsn saveMsn : limitedSaveMsns) {
            saveMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            saveMsnRepository.save(saveMsn);
        }
        return true;
    }

    public boolean setOffMissionIsUsed(int idx,List<SaveMsn> target) {

        for (SaveMsn saveMsn : target) {
            saveMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            saveMsnRepository.save(saveMsn);
        }
        return true;
    }

    public boolean setOffMissionIsView(int idx) {
        List<SaveMsn> saveMsns = getSaveMsns();
        Collections.reverse(saveMsns);

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (idx - 1) * limit;

        // 전체 리스트의 크기 체크
        List<SaveMsn> limitedSaveMsns;
        if (startIndex < saveMsns.size()) {
            int endIndex = Math.min(startIndex + limit, saveMsns.size());
            limitedSaveMsns = saveMsns.subList(startIndex, endIndex);
        } else {
            return false;
        }
        for (SaveMsn saveMsn : limitedSaveMsns) {
            saveMsn.setMissionExposure(false); // missionExpose 필드를 false로 설정
            saveMsnRepository.save(saveMsn);
        }
        return true;
    }
    public boolean setOffMissionIsView(int idx,List<SaveMsn> target) {


        for (SaveMsn saveMsn : target) {
            saveMsn.setMissionExposure(false); // missionExpose 필드를 false로 설정
            saveMsnRepository.save(saveMsn);
        }
        return true;
    }

    public boolean AllOffMission() {
        List<SaveMsn> saveMsns = getSaveMsns();
        for (SaveMsn saveMsn : saveMsns) {
            saveMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            saveMsn.setMissionExposure(false);
            saveMsnRepository.save(saveMsn);
        }
        return true;
    }

    public Sheet excelDownloadCurrent(List<SaveMsn> list, Workbook wb) {
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
        cell.setCellValue("광고주");
        sheet.setColumnWidth(1, 16 * 256); //8자
        cell.setCellStyle(cellStyle);


        cell = row.createCell(2);
        cell.setCellValue("미션 제목");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(2, 16 * 256);

        cell = row.createCell(3);
        cell.setCellValue("기본 수량");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("데일리캡");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("전체 랜딩수");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("전체 참여수");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cell.setCellValue("미션 시작일시");
        sheet.setColumnWidth(6, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(8);
        cell.setCellValue("미션 종료일시");
        sheet.setColumnWidth(7, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(9);
        cell.setCellValue("데일리캡 시작일");
        sheet.setColumnWidth(8, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(10);
        cell.setCellValue("데일리캡 종료일");
        sheet.setColumnWidth(9, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(11);
        cell.setCellValue("미션 상태");
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
            cell.setCellValue(saveMsn.getAdvertiserDetails());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(saveMsn.getMissionTitle());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(3);
            cell.setCellValue(saveMsn.getMissionDefaultQty());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(4);
            cell.setCellValue(saveMsn.getMissionDailyCap());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(5);
            cell.setCellValue(saveMsn.getTotalLandingCnt());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(6);
            cell.setCellValue(saveMsn.getTotalPartCnt());
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
            cell.setCellStyle(cellStyle);

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
            if(saveMsn.getReEngagementDay()!=null)
                cell.setCellValue(saveMsn.getReEngagementDay());
            else
                cell.setCellValue("-");
        }
        return sheet;
    }

    public boolean setMissionIsViewFalse(int idx) {
        SaveMsn target= saveMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionExposure(false);
        saveMsnRepository.save(target);
        return true;
    }

    public boolean setMissionIsView(int idx) {
        SaveMsn target= saveMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionExposure(true);
        saveMsnRepository.save(target);
        return true;
    }

    public boolean setMissionIsUsedFalse(int idx) {
        SaveMsn target= saveMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionActive(false);
        saveMsnRepository.save(target);
        return true;
    }

    public boolean setMissionIsUsed(int idx) {
        SaveMsn target= saveMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionActive(true);
        saveMsnRepository.save(target);
        return true;
    }

    public boolean changeMissionReEngagementDay(int idx, SaveMsnAbleDayDto dto) {
        SaveMsn target = saveMsnRepository.findByIdx(idx);
        if(target ==null)
            return false;
        target.setDupParticipation(dto.isDupParticipation());
        if(!dto.isDupParticipation())
            target.setReEngagementDay(null);
        else
            target.setReEngagementDay(dto.getReEngagementDay());

        saveMsnRepository.save(target);
        return true;
    }

    public List<SaveMsn> searchSaveMsnCurrent(SaveMsnSearchByConsumedDto dto) {
        List<SaveMsn> target_advertiser = null;
        List<SaveMsn> target_serverUrl = null;
        List<SaveMsn> target_advertiser_details = null; // 선택 1
        List<SaveMsn> target_mission_title = null; // 선택 2

        List<SaveMsn> result;
        boolean changed = false;

        if(dto.getAdvertiser()!=null){
            target_advertiser = saveMsnRepository.findByAdvertiser(dto.getAdvertiser());
        }

        if(dto.getServerUrl()!=null){
            target_serverUrl = saveMsnRepository.findByServer(dto.getServerUrl());
        }

        if(dto.getAdvertiserDetails() != null  && !dto.getAdvertiserDetails().isEmpty())
            target_advertiser_details = saveMsnRepository.findByAdvertiserDetails(dto.getAdvertiserDetails());

        if(dto.getMissionTitle() != null && !dto.getMissionTitle().isEmpty())
            target_mission_title = saveMsnRepository.findByMissionTitle(dto.getMissionTitle());

        LocalDateTime now = LocalDateTime.now();
        result = new ArrayList<>(saveMsnRepository.findByCurrentList(now));


        if(target_serverUrl !=null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser != null) {
            Set<Integer> idxSet = target_advertiser.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_advertiser_details != null) {
            Set<Integer> idxSet = target_advertiser_details.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        else if(target_mission_title !=null) {
            Set<Integer> idxSet = target_mission_title.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();
        return result;
    }

    public boolean AllOffMissionCurrent() {
        LocalDateTime now = LocalDateTime.now();
        List<SaveMsn> saveMsns = saveMsnRepository.findByCurrentList(now);
        for (SaveMsn saveMsn : saveMsns) {
            saveMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            saveMsn.setMissionExposure(false);
            saveMsnRepository.save(saveMsn);
        }
        return true;
    }


    public Sheet reportExcelDownload(List<SaveMsnDailyStat> list, Workbook wb) {
        int size = list.size();
        Sheet sheet = wb.createSheet("정답 미션 목록");
        Row row = null;
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("일시");
        sheet.setColumnWidth(1, 16 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("랜딩 카운트");
        sheet.setColumnWidth(1, 16 * 256); //8자
        cell.setCellStyle(cellStyle);


        cell = row.createCell(2);
        cell.setCellValue("참여 카운트");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(2, 16 * 256);

        for (SaveMsnDailyStat saveMsnDailyStat : list) {

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cell.setCellValue(saveMsnDailyStat.getPartDate().format(formatter_));
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue(saveMsnDailyStat.getLandingCnt());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(saveMsnDailyStat.getPartCnt());
            cell.setCellStyle(cellStyle);

        }
        return sheet;
    }

    public List<SaveMsnDailyStat> searchReport(sightseeingStaticListSearchDto dto,int idx) {

        List<SaveMsnDailyStat> target_url = null;
        List<SaveMsnDailyStat> target_date = null;
        List<SaveMsnDailyStat> target_idx = saveMsnDailyStatRepository.findByMsnIdx_(idx);


        List<SaveMsnDailyStat> result;
        boolean changed = false;

        if(dto.getStartDate() != null || dto.getEndDate() != null) {
            if (dto.getStartDate() != null) {
                if (dto.getEndDate() == null) {
                    target_date  = saveMsnDailyStatRepository.findByStartAt(dto.getStartDate());
                } else {
                    target_date  = saveMsnDailyStatRepository.findByBothAt(dto.getStartDate(), dto.getEndDate());
                }

            } else {
                target_date  = saveMsnDailyStatRepository.findByEndAt(dto.getEndDate());
            }
        }

        if(dto.getUrl() != null)
            target_url = saveMsnDailyStatRepository.findByServer_ServerUrl(dto.getUrl());




        result = new ArrayList<>(saveMsnDailyStatRepository.findAll());

        if(target_idx  !=null) {
            Set<Integer> idxSet = target_idx.stream().map(SaveMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDailyStat -> idxSet.contains(saveMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(target_date  !=null) {
            Set<Integer> idxSet = target_date.stream().map(SaveMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDailyStat -> idxSet.contains(saveMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_url  !=null) {
            Set<Integer> idxSet = target_url.stream().map(SaveMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsnDailyStat -> idxSet.contains(saveMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(!changed)
            result = new ArrayList<>();

        return result;
    }
    public Sheet downloadSightseeingForm(Workbook wb) {


        Sheet sheet = wb.createSheet("저장 미션 목록");
        Row row = null;
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("quizIdx(공란)");
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


        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("5");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("5");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("시크릿 K");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("-");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("title");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("search keyword");
        cell.setCellStyle(cellStyle);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        cell = row.createCell(7);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("2024-01-01 00:00:00");

        cell = row.createCell(8);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("2024-01-01 00:00:00");


        cell = row.createCell(10);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("2024-01-01");

        cell = row.createCell(11);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("2024-01-01");

        cell = row.createCell(11);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("활성/비활성");


        cell = row.createCell(12);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("노출/비노출");

        cell = row.createCell(13);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("중복 허용/중복 불가");

        cell = row.createCell(14);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("5");

        return sheet;
    }

    public List<SaveMsn> findByCurrentListAffiliate(LocalDateTime now, int aidx) {

        return saveMsnRepository.findByCurrentListAffiliate(now,aidx);
    }

    public List<SaveMsn> searchSaveMsnCurrentByAffiliate(List<SaveMsn> target, MediaCompanySightseeingCurrentSearchDto dto) {


        List<SaveMsn> target_mission_title = null; // 선택 2



        List<SaveMsn> result;
        boolean changed = false;

        if(dto.getMissionTitle() != null && !dto.getMissionTitle().isEmpty())
            target_mission_title = saveMsnRepository.findByMissionTitle(dto.getMissionTitle());


        result = target;

        if(target_mission_title !=null) {
            Set<Integer> idxSet = target_mission_title.stream().map(SaveMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(saveMsn -> idxSet.contains(saveMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(!changed)
            result = new ArrayList<>();
        return result;
    }
}

package mx.uatx.siia.citas.controller;

import com.google.gson.Gson;
import mx.uatx.siia.citas.MisCitas;
import mx.uatx.siia.citas.SIMSCITAS;
import mx.uatx.siia.citas.Tramites.business.TramitesBusiness;
import mx.uatx.siia.citas.areas.business.AreasBusiness;
import mx.uatx.siia.citas.areas.business.SiPaAreasConfiguraciones;
import mx.uatx.siia.citas.citasBusiness.CitaBusiness;
import mx.uatx.siia.citas.citasBusiness.MethodsGenerics;
import mx.uatx.siia.citas.citasBusiness.ObjectMapperUtils;
import mx.uatx.siia.citas.dto.ConfiguacionesDTO;
import mx.uatx.siia.citas.enums.EstatusCitas;
import mx.uatx.siia.citas.enums.URLs;
import mx.uatx.siia.citas.models.Eventos;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.reportes.GeneriReportFields;
import mx.uatx.siia.serviciosUniversitarios.dto.CitasTO;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ViewScoped
@ManagedBean(name = "admin")
public class AdminCitasBean implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2621529818431646329L;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ManagedProperty("#{msj}")
    private ResourceBundle msj;

    @ManagedProperty("#{citaBusiness}")
    private CitaBusiness citaBusiness;

    @ManagedProperty("#{tramitesBusiness}")
    private TramitesBusiness tramitesBusiness;

    @ManagedProperty("#{areasBusiness}")
    private AreasBusiness areasBusiness;

    private final VistasHelper vHelp = new VistasHelper();

    private List<String> listMeses;
    private List<String> listDias;
    private List<MisCitas> listCitas;
    private List<SelectItem> listTramites;
    private List<String> listDaysToCheck;
    private List<String> listDayswasRemoved;
    private List<SelectItem> listEstatus;
    private List<Eventos> listEventos;
    private String mesActual;
    private String strDia;
    private String strIdArea;
    private String strkindTramite;
    private String strLocalNameTramite;
    private String strCurrentTramite;
    private String strCalendarValue;
    private String strDateDisablesCalendar;
    private String strCurrentEstatus;
    private String strHourServiceStar;
    private String strHourServiceEnd;
    private String strCurrentLocalDate;
    private String strDuracionCita;
    private String strValueDateFieldA;
    private String strValueDateFieldB;
    private final String strUser;
    private String strlang;

    private boolean hasDataTramites = false;
    private boolean hasComboStatus = false;
    private boolean hasUpdateListHorarios = false;
    private boolean wasDayDisable = true;
    private boolean hasDiastoDisable = false;
    private boolean switchMode = false;
    private String strIDH;

    public AdminCitasBean(){
        strIDH = "30643";
        strIdArea = "65";
        strUser = "20082306"; // TODO cambiar por las datos del usuario.
    }

    public void getDaysDisable(){
        logger.info("----- Generando Fechas no abiles");
        ResultadoTO resultado = areasBusiness.obtenerDiasInhabiles(strIdArea);
        strDateDisablesCalendar = MethodsGenerics.formattingStringFechasCalendar( (List<String>) resultado.getObjeto());
    }

    @PostConstruct
    public void innit(){
        getDaysDisable();
        logger.info("Getting Setting from Area [idArea] =>"+strIdArea);
        ResultadoTO resultado = areasBusiness.obtenerConfiguracionArea(strIdArea);
        SiPaAreasConfiguraciones dataConfig = (SiPaAreasConfiguraciones) resultado.getObjeto();

        ConfiguacionesDTO configuacionesDTOS = ObjectMapperUtils.map(dataConfig, ConfiguacionesDTO.class);

        strDuracionCita = configuacionesDTOS.getDuracionCitas();
        strHourServiceEnd = configuacionesDTOS.getHoraServicioFin();
        strHourServiceStar = configuacionesDTOS.getHoraServicioInicio();

        ResultadoTO resultadoTO = areasBusiness.obtenerEventos(strIdArea);
        List<SIMSCITAS> localList = (List<SIMSCITAS>) resultadoTO.getObjeto();

        if (!localList.isEmpty()){
            listEventos = new ArrayList<>();
            List<CitasTO> citas = ObjectMapperUtils.mapAll(localList, CitasTO.class);

            for (CitasTO misCitas : citas){
                String[] params = new String[]{
                        misCitas.getIntIdCita().toString(),
                        misCitas.getLongHistorialAcademico().toString(),
                        strIdArea,
                        misCitas.getStrEstatus().equals(EstatusCitas.CitaAgendada.getValor()) ? "#3a87ad" : ( misCitas.getStrEstatus().equals(EstatusCitas.CitaCompleta.getValor()) ? "#2ecc71" : (misCitas.getStrEstatus().equals(EstatusCitas.CitaCancelada.getValor()) ? "#2c3e50" : ( misCitas.getStrEstatus().equals(EstatusCitas.CitaPospuesta.getValor()) ? "#8e44ad" : "#ff7f50" ) ) )
                };
                listEventos.add(new Eventos(
                        "Cita de " + misCitas.getStrNombreUser() +" - "+ misCitas.getIntMatricula(),
                        MethodsGenerics.getDateToFullCalendar(misCitas.getStrFechaReservada() + " " + misCitas.getStrHoraReservada()),
                        params
                ));
            }
        }
        strlang = "es";
    }

    public void saveDataA(){
        logger.info("--- Guardando datos de la configuracion ---");

        String[] params =new String[]{strHourServiceStar, strHourServiceEnd, strDuracionCita};
        ResultadoTO resultado = areasBusiness.guardarConfiguracionesArea(Long.parseLong(strIdArea), params);

        if (resultado.isBlnValido()){
            resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.admin.savedataconfig.ok");
            vHelp.pintarMensajes(msj,resultado);
        }else{
            resultado.agregarMensaje(SeveridadMensajeEnum.ERROR, "comun.msj.citas.admin.savedataconfig.error");
            vHelp.pintarMensajes(msj,resultado);
        }
    }

    public void saveDataB() {
        logger.info("---- Guardar datos de la lista de dias desactivados --- ");

        HashMap<String, Object> datacollect = new HashMap<>();
        datacollect.put("lista", listDayswasRemoved);
        datacollect.put("idarea", strIdArea);
        datacollect.put("fecha", strCurrentLocalDate);
        datacollect.put("user", strUser);

       if (hasUpdateListHorarios){
           ResultadoTO resultado = citaBusiness.saveDataDB(datacollect, URLs.InsertData.getValor()+"?savedays=true");
           Map<String, Object> response = (Map<String, Object>) resultado.getObjeto();

           if (response.get("codefromservice").equals("1")){
               resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.admin.savedataconfig.ok");
               vHelp.pintarMensajes(msj, resultado);
           }else{
               resultado.agregarMensaje(SeveridadMensajeEnum.ERROR, "comun.msj.citas.admin.savedataconfig.error");
               vHelp.pintarMensajes(msj, resultado);
           }
       }else{
           ResultadoTO resultado = new ResultadoTO();
           resultado.agregarMensaje(SeveridadMensajeEnum.ALERTA,"comun.info.citasadmin.listhorariosnoupdate");
           vHelp.pintarMensajes(msj, resultado);
       }
    }


    public void disableDay(){
        String[] params = new String[]{strIdArea, strCurrentLocalDate, "20082306"};
        ResultadoTO resultado = areasBusiness.desactivarDia(params);
        if ((boolean) resultado.getObjeto()) {
            resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msg.citas.daysave.ok");
            vHelp.pintarMensajes(msj, resultado);
            wasDayDisable = false;
        } else {
            resultado.agregarMensaje(SeveridadMensajeEnum.ERROR, "comun.msg.citas.daysave.error");
            vHelp.pintarMensajes(msj, resultado);
        }
    }

    /**
     * @apiNote Metodo temporal para tipos de reportes
     * @return List<SelectItems>
     */

    public List<SelectItem> getListKindReportes(){
        List<SelectItem> selectItems = new ArrayList<>();
        selectItems.add(0, new SelectItem("1", "Reporte por tipo de tramite"));
        selectItems.add(1, new SelectItem("2", "Reporte global de cita"));
        selectItems.add(2, new SelectItem("3", "Reporte por estatus"));

        return selectItems;
    }

    public List<SelectItem> getListKindOfEstatus(){
        List<SelectItem> selectItems = new ArrayList<>();
        selectItems.add(0, new SelectItem(EstatusCitas.CitaAgendada.getValor(),EstatusCitas.CitaAgendada.getValor()));
        selectItems.add(1, new SelectItem(EstatusCitas.CitaPospuesta.getValor(),EstatusCitas.CitaPospuesta.getValor()));
        selectItems.add(2, new SelectItem(EstatusCitas.CitaCancelada.getValor(),EstatusCitas.CitaCancelada.getValor()));
        selectItems.add(3, new SelectItem(EstatusCitas.CitaCaducada.getValor(),EstatusCitas.CitaCaducada.getValor()));
        selectItems.add(4, new SelectItem(EstatusCitas.CitaCompleta.getValor(),EstatusCitas.CitaCompleta.getValor()));

        return selectItems;
    }

    public void listerpostReporte(){
        if (strkindTramite.equals("1")){
            ResultadoTO res = tramitesBusiness.obtenerTramites(Integer.parseInt(strIdArea));
            setListTramites((List<SelectItem>) res.getObjeto());
            if (res.isBlnValido()){
                hasDataTramites = true;
            }
        }else if (strkindTramite.equals("3")){
            hasComboStatus = true;
            listEstatus = getListKindOfEstatus();
        }else{
            hasDataTramites=false;
            hasComboStatus =false;
        }
    }
    public void generarReporte(){
        ResultadoTO resultado;
        HashMap<String, Object> parameters = new HashMap<>();
        List<GeneriReportFields> extraParams = null;
        JRBeanCollectionDataSource colDat;

        String fechalocal = MethodsGenerics.getCurrentDate();
        String nameFile = null;

        switch (strkindTramite){
            case "1":
                resultado = citaBusiness.GenerarReportePorTipoTramite(Long.parseLong(strCurrentTramite), Long.parseLong(strIdArea), MethodsGenerics.formtDateDB(strValueDateFieldA), MethodsGenerics.formtDateDB(strValueDateFieldB));
                colDat = new JRBeanCollectionDataSource((List<MisCitas>) resultado.getObjeto());
                parameters.put("JRBeanCollectionData", colDat);

                nameFile = "ReporteCitas";
                extraParams = new ArrayList<>();
                extraParams.add(0, new GeneriReportFields("Departamento de Registro y control escolar",
                        strLocalNameTramite,
                        fechalocal,
                        "Reporte por tipo de tramite",
                        MethodsGenerics.formatDate(strValueDateFieldA)+" al "+MethodsGenerics.formatDate(strValueDateFieldB)));
                break;
            case "2":
                nameFile = "ReporteCitas";
                resultado = citaBusiness.GenerarReporteCitasGlobal(Long.parseLong(strIdArea), MethodsGenerics.formtDateDB(strValueDateFieldA), MethodsGenerics.formtDateDB(strValueDateFieldB));
                colDat = new JRBeanCollectionDataSource((List<MisCitas>) resultado.getObjeto());
                parameters.put("JRBeanCollectionData", colDat);
                extraParams = new ArrayList<>();
                extraParams.add(0, new GeneriReportFields("Departamento de Registro y control escolar",
                        strLocalNameTramite,
                        fechalocal,
                        "Reporte global de citas",
                        MethodsGenerics.formatDate(strValueDateFieldA)+" al "+MethodsGenerics.formatDate(strValueDateFieldB)));
        }
        downloadPDF(nameFile, extraParams, parameters);
    }

    public void downloadPDF(String nameFile, List<?> lista, HashMap<String, Object> parameters ){
        String ruta = "resources/reportes/citas";
        vHelp.llenarYObtenerBytesReporteJasperPDF(ruta, nameFile, lista, parameters);
    }


    public void listenergetnameTramite(){
        for (SelectItem list : listTramites) {
            if (list.getValue() == strkindTramite)
                strLocalNameTramite = list.getLabel();
        }
    }

    public void RenderDaysToCalendar(){
        logger.info("---- Render days");
        listDayswasRemoved = new ArrayList<>();
        ResultadoTO resultado = citaBusiness.getHoursOfCalendarDisable(Long.parseLong(strIdArea), MethodsGenerics.formatDate(strCalendarValue));
        List<String> fromDBhorarios = (List<String>) resultado.getObjeto();

        strCurrentLocalDate = MethodsGenerics.formatDate(strCalendarValue);

        listDaysToCheck = MethodsGenerics.generarHorarios(
                Integer.parseInt(strHourServiceStar.replace(':', '0'))/1000,
                Integer.parseInt(strHourServiceEnd.replace(':','0'))/1000,
                Integer.parseInt(strDuracionCita),
                fromDBhorarios
        );
        wasDayDisable=true;
        hasDiastoDisable=true;
    }

    public void deletefromlistdays(String strCalendarValue){
        logger.info("to delete:"+strCalendarValue);
        listDayswasRemoved.add(strCalendarValue);
        boolean ope = listDaysToCheck.remove(strCalendarValue);
        ResultadoTO resultado = new ResultadoTO();
        if (ope) {
            resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.fechas.ok");
            hasUpdateListHorarios = true;
        }
        else
            resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.fechas.loaderror");

        vHelp.pintarMensajes(msj, resultado);
    }

    public void rollbackday(String dayremove)
    {
        ResultadoTO resultado = new ResultadoTO();
        listDaysToCheck.add(dayremove);
        resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.fechas.ok");
        listDayswasRemoved.remove(dayremove);
    }

    public String toJson(){
        return new Gson().toJson(listEventos);
    }



    /**
     * @apiNote Metoddos Setter and Getters.
     */
    public String getStrDateDisablesCalendar() {
        return strDateDisablesCalendar;
    }
    public void setStrDateDisablesCalendar(String strDateDisablesCalendar) {
        this.strDateDisablesCalendar = strDateDisablesCalendar;
    }
    public String getStrkindTramite() {
        return strkindTramite;
    }
    public String getStrCurrentTramite() {
        return strCurrentTramite;
    }
    public String getStrValueDateFieldA() {
        return strValueDateFieldA;
    }
    public void setStrValueDateFieldA(String strValueDateFieldA) {
        this.strValueDateFieldA = strValueDateFieldA;
    }

    public String getStrValueDateFieldB() {
        return strValueDateFieldB;
    }

    public void setStrValueDateFieldB(String strValueDateFieldB) {
        this.strValueDateFieldB = strValueDateFieldB;
    }

    public TramitesBusiness getTramitesBusiness() {
        return tramitesBusiness;
    }
    public void setTramitesBusiness(TramitesBusiness tramitesBusiness) {this.tramitesBusiness = tramitesBusiness;}
    public void setStrCurrentTramite(String strCurrentTramite) {
        this.strCurrentTramite = strCurrentTramite;
    }
    public void setStrkindTramite(String strkindTramite) {
        this.strkindTramite = strkindTramite;
    }
    public List<SelectItem> getListTramites() {
        return listTramites;
    }
    public void setListTramites(List<SelectItem> listTramites) {
        this.listTramites = listTramites;
    }
    public boolean isHasDataTramites() {
        return hasDataTramites;
    }
    public void setHasDataTramites(boolean hasDataTramites) {
        this.hasDataTramites = hasDataTramites;
    }
    public String getStrCalendarValue() {
        return strCalendarValue;
    }
    public void setStrCalendarValue(String strCalendarValue) {
        this.strCalendarValue = strCalendarValue;
    }
    public String getStrDuracionCita() {
        return strDuracionCita;
    }
    public void setStrDuracionCita(String strDuracionCita) {
        this.strDuracionCita = strDuracionCita;
    }
    public CitaBusiness getCitaBusiness() {
        return citaBusiness;
    }
    public void setCitaBusiness(CitaBusiness citaBusiness) {this.citaBusiness = citaBusiness;}
    public String getStrIdArea() {
        return strIdArea;
    }
    public List<String> getListDaysToCheck() {
        return listDaysToCheck;
    }
    public boolean isHasComboStatus() {return hasComboStatus;}
    public void setHasComboStatus(boolean hasComboStatus) {
        this.hasComboStatus = hasComboStatus;
    }
    public void setListDaysToCheck(List<String> listDaysToCheck) {
        this.listDaysToCheck = listDaysToCheck;
    }
    public void setStrIdArea(String strIdArea) {
        this.strIdArea = strIdArea;
    }
    public AreasBusiness getAreasBusiness() {
        return areasBusiness;
    }
    public void setAreasBusiness(AreasBusiness areasBusiness) {
        this.areasBusiness = areasBusiness;
    }
    public Logger getLogger() {
        return logger;
    }
    public List<String> getListMeses() {
        return listMeses;
    }
    public String getMesActual() {
        return mesActual;
    }
    public void setMesActual(String mesActual) {
        this.mesActual = mesActual;
    }
    public String getStrDia() {return strDia;}
    public void setStrDia(String strDia) {this.strDia = strDia;}
    public List<MisCitas> getListCitas() {
        return listCitas;
    }
    public void setListCitas(List<MisCitas> listCitas) {
        this.listCitas = listCitas;
    }
    public List<String> getListDias() {
        return listDias;
    }
    public void setListDias(List<String> listDias) {
        this.listDias = listDias;
    }
    public String getStrHourServiceStar() {
        return strHourServiceStar;
    }
    public void setStrHourServiceStar(String strHourServiceStar) {
        this.strHourServiceStar = strHourServiceStar;
    }
    public String getStrHourServiceEnd() {
        return strHourServiceEnd;
    }
    public void setStrHourServiceEnd(String strHourServiceEnd) {
        this.strHourServiceEnd = strHourServiceEnd;
    }
    public void setListMeses(List<String> listMeses) {
        this.listMeses = listMeses;
    }
    public ResourceBundle getMsj() {
        return msj;
    }
    public String getStrCurrentLocalDate() {
        return strCurrentLocalDate;
    }
    public void setStrCurrentLocalDate(String strCurrentLocalDate) {
        this.strCurrentLocalDate = strCurrentLocalDate;
    }
    public void setMsj(ResourceBundle msj) {
        this.msj = msj;
    }
    public List<String> getListDayswasRemoved() {
        return listDayswasRemoved;
    }
    public void setListDayswasRemoved(List<String> listDayswasRemoved) {
        this.listDayswasRemoved = listDayswasRemoved;
    }
    public List<Eventos> getListEventos() {
        return listEventos;
    }
    public void setListEventos(List<Eventos> listEventos) {
        this.listEventos = listEventos;
    }
    public String getStrlang() {
        return strlang;
    }
    public void setStrlang(String strlang) {
        this.strlang = strlang;
    }
    public boolean isWasDayDisable() {
        return wasDayDisable;
    }
    public void setWasDayDisable(boolean wasDayDisable) {
        this.wasDayDisable = wasDayDisable;
    }
    public boolean isHasDiastoDisable() {
        return hasDiastoDisable;
    }
    public void setHasDiastoDisable(boolean hasDiastoDisable) {
        this.hasDiastoDisable = hasDiastoDisable;
    }

    public boolean isSwitchMode() {
        return switchMode;
    }

    public void setSwitchMode(boolean switchMode) {
        this.switchMode = switchMode;
    }

    public String getStrIDH() {
        return strIDH;
    }

    public String getStrCurrentEstatus() {
        return strCurrentEstatus;
    }

    public void setStrCurrentEstatus(String strCurrentEstatus) {
        this.strCurrentEstatus = strCurrentEstatus;
    }

    public List<SelectItem> getListEstatus() {
        return listEstatus;
    }

    public void setListEstatus(List<SelectItem> listEstatus) {
        this.listEstatus = listEstatus;
    }

    public void setStrIDH(String strIDH) {
        this.strIDH = strIDH;
    }
}
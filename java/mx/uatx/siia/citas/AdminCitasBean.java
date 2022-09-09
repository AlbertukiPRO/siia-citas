package mx.uatx.siia.citas;

import mx.uatx.siia.citas.modelo.MisCitas;
import mx.uatx.siia.citas.modelo.Tramites.business.TramitesBusiness;
import mx.uatx.siia.citas.modelo.areas.business.AreasBusiness;
import mx.uatx.siia.citas.modelo.areas.business.SiPaAreasConfiguraciones;
import mx.uatx.siia.citas.modelo.citasBusiness.CitaBusiness;
import mx.uatx.siia.citas.modelo.citasBusiness.MethodsGenerics;
import mx.uatx.siia.citas.modelo.enums.ServiciosReportes;
import mx.uatx.siia.citas.modelo.enums.URLs;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.reportes.GeneriReportFields;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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

    private final VistasHelper vHelp = new VistasHelper();

    private String anoActual;
    private List<String> listMeses;
    private List<String> listDias;
    private List<MisCitas> listCitas;
    private List<SelectItem> listTramites;
    private List<String> listDaysToCheck;

    @ManagedProperty("#{tramitesBusiness}")
    private TramitesBusiness tramitesBusiness;

    @ManagedProperty("#{areasBusiness}")
    private AreasBusiness areasBusiness;
    private int today;
    private String mesActual;
    private String strDia;
    private String strIdArea;
    private String strkindTramite;
    private String strLocalNameTramite;
    private String strCurrentTramite;
    private String strCalendarValue;
    private String strDateDisablesCalendar;

    private String strHourServiceStar;
    private String strHourServiceEnd;
    private String strCurrentLocalDate;
    private String strDuracionCita;
    private String strValueDateField;

    private boolean hasDataTramites = false;
    private boolean hasFielDate = false;

    public AdminCitasBean(){
        listMeses = Arrays.asList("Enero", "Febrero", "Marzo","Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre","Octubre", "Noviembre", "Diciembre");
        GenerarFechas(Calendar.getInstance().get(Calendar.MONTH));
        strIdArea = "1";
        LocalDate todaylocal = LocalDate.now();
        today = todaylocal.getDayOfMonth();
        mesActual = GetIndexMes(todaylocal.getMonthValue()-1);

        System.out.println("--- Today => "+today);
        System.out.println("--- Month => "+mesActual);

        getDayString();
        obtenerCitasGlobales();
    }

    public void getDaysDisable(){
        System.out.println("----- Getting days disable");
        ResultadoTO resultado = areasBusiness.obtenerFechasFromDB(URLs.FechasReservadas.getValor(), strIdArea);
        strDateDisablesCalendar = MethodsGenerics.formattingStringFechasCalendar( (List<String>) resultado.getObjeto());
    }

    @PostConstruct
    public void getSettingsArea(){
        getDaysDisable();
        System.out.println("------- Getting Setting from Area [idArea] => "+strIdArea);
        ResultadoTO resultado = areasBusiness.obtenerConfiguracionArea(strIdArea);
        List<SiPaAreasConfiguraciones> data = (List<SiPaAreasConfiguraciones>) resultado.getObjeto();

        strDuracionCita = data.get(0).getDuracionCitas();
        strHourServiceEnd = data.get(0).getHoraServicioFin();
        strHourServiceStar = data.get(0).getHoraServicioInicio();
    }

    public void saveDataA(){
        System.out.println("--- Calling to save data configuración---");

        HashMap<String, String> datacollect = new HashMap<>();

        datacollect.put("idarea", strIdArea);
        datacollect.put("horainicion", strHourServiceStar);
        datacollect.put("horafin", strHourServiceEnd);
        datacollect.put("duracion", strDuracionCita);

        ResultadoTO resultado = citaBusiness.agendarCita(datacollect, URLs.InsertData.getValor()+"?savesetting=true");
        Map<String, Object> response = (Map<String, Object>) resultado.getObjeto();

        if (response.get("codefromservice").equals("1")){
            resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.admin.savedataconfig.ok");
            vHelp.pintarMensajes(msj,resultado);
        }else{
            resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.admin.savedataconfig.error");
            vHelp.pintarMensajes(msj,resultado);
        }
    }

    public List<SelectItem> getListKindReportes(){
        List<SelectItem> selectItems = new ArrayList<>();
        selectItems.add(0, new SelectItem("1", "Reporte por tipo de tramite"));
        selectItems.add(1, new SelectItem("2", "Reporte global de cita"));
        selectItems.add(2, new SelectItem("3", "Reporte por fecha"));

        return selectItems;
    }

    public void generarReporte(){

        ResultadoTO resultado;
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        List<MisCitas> misCitas = null;
        List<GeneriReportFields> lista = null;
        JRBeanCollectionDataSource colDat;
        String[] params;

        String fechalocal = MethodsGenerics.getCurrentDate();
        String nameFile = null;

        switch (strkindTramite){
            case "1":
                 params = new String[]{"1"};
                resultado = citaBusiness.getMisCitasOfService(params, ServiciosReportes.GetAllCitasOnArea.getValor());
                misCitas = (List<MisCitas>) resultado.getObjeto();
                colDat = new JRBeanCollectionDataSource(misCitas);
                parameters.put("JRBeanCollectionData", colDat);

                nameFile = "ReporteCitasGlobal";
                lista = new ArrayList<>();
                lista.add(0, new GeneriReportFields("Departamento de Registro y control escolar", fechalocal));
                break;

            case "2":
                params = new String[]{"1", strkindTramite};

                resultado = citaBusiness.getMisCitasOfService(params, ServiciosReportes.GetAllCitasOnTramite.getValor());
                misCitas = (List<MisCitas>) resultado.getObjeto();
                colDat = new JRBeanCollectionDataSource(misCitas);
                parameters.put("JRBeanCollectionData", colDat);

                nameFile = "ReporteCitaOnTramite";
                lista = new ArrayList<>();
                lista.add(0, new GeneriReportFields("Departamento de Registro y control escolar", strLocalNameTramite, fechalocal, ""));
                break;

            case "3":
                params = new String[]{"1"};
                resultado = citaBusiness.getMisCitasOfService(params, ServiciosReportes.GetAllCitasOnFecha.getValor());
                misCitas = (List<MisCitas>) resultado.getObjeto();
                colDat = new JRBeanCollectionDataSource(misCitas);
                parameters.put("JRBeanCollectionData", colDat);

                nameFile = "ReporteCitaOnTramite";
                lista = new ArrayList<>();
                lista.add(0, new GeneriReportFields("Departamento de Registro y control escolar", strLocalNameTramite, fechalocal, ""));
                break;


        }

        String ruta = "resources/reportes/citas";

        // before pass misCitas var.
        vHelp.llenarYObtenerBytesReporteJasperPDF(ruta, nameFile, lista, parameters);
    }

    public void listerpostReporte(){
        if (strkindTramite.equals("1")){
            ResultadoTO res = tramitesBusiness.obtenerTramites(URLs.Tramites.getValor(), strIdArea);
            setListTramites((List<SelectItem>) res.getObjeto());
            if (res.isBlnValido()){
                hasDataTramites = true;
            }
        }
    }

    public void listenergetnameTramite(){
        for (SelectItem list : listTramites) {
            if (list.getValue() == strkindTramite)
                strLocalNameTramite = list.getLabel();
        }
    }

    public void RenderDaysToCalendar(){
        System.out.println("---- Render days");
        ResultadoTO resultado = citaBusiness.getHoursOfCalendarDisable(strIdArea, MethodsGenerics.formatDate(strCalendarValue));
        List<String> fromDBhorarios = (List<String>) resultado.getObjeto();

        strCurrentLocalDate = MethodsGenerics.formatDate(strCalendarValue);

        listDaysToCheck = MethodsGenerics.generarHorarios(
                Integer.parseInt(strHourServiceStar.replace(':', '0'))/1000,
                Integer.parseInt(strHourServiceEnd.replace(':','0'))/1000,
                Integer.parseInt(strDuracionCita),
                fromDBhorarios
        );
    }

    public void deletefromlistdays(String strCalendarValue){
        System.out.println("to delete: "+strCalendarValue);
        listDaysToCheck.remove(strCalendarValue);
    }

    private void getDayString(){

        try {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Date dt1=format.parse("05/26/2022");
            DateFormat format2=new SimpleDateFormat("EEEE");
            String finalDay=format2.format(dt1);
            System.out.println(finalDay);

            this.strDia = finalDay;
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void obtenerCitasGlobales(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();
        listCitas = ServicesCitas.getCitasFromArea("http://localhost/siiaServices/apis/misCitas.php",strIdArea,dtf.format(now));
    }

    public void obtenerCitasFromDia(String dia){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String fecha = dtf.format(now);
        String subStrin[] = fecha.split("/");
        listCitas = ServicesCitas.getCitasFromArea("http://localhost/siiaServices/apis/misCitas.php",strIdArea,subStrin[0]+"/"+dia+"/"+subStrin[2]);
    }

    public void GenerarFechas(int mes){
        listDias = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(2022,mes+1);
        int daysInMonth = yearMonth.lengthOfMonth();

        for (int i = 1; i <= daysInMonth; i++) {
            listDias.add(i <= 9 ? "0"+i : Integer.toString(i));
        }
        System.out.println("--- FECHAS GENERADAS : "+listDias);
    }

    public void RefrescarFechas(String mes){
        System.out.println("--- SEND "+mes+" To Generate Function");
        switch (mes){
            case "Enero":
                GenerarFechas(0);
                break;
            case "Febrero":
                GenerarFechas(1);
                break;
            case "Marzo":
                GenerarFechas(2);
                break;
            case "Abril":
                GenerarFechas(3);
                break;
            case "Mayo":
                GenerarFechas(4);
                break;
            case "Junio":
                GenerarFechas(5);
                break;
            case "Julio":
                GenerarFechas(6);
                break;
            case "Agosto":
                GenerarFechas(7);
                break;
            case "Septiembre":
                GenerarFechas(8);
                break;
            case "Octubre":
                GenerarFechas(9);
                break;
            case "Noviembre":
                GenerarFechas(10);
                break;
            case "Diciembre":
                GenerarFechas(11);
                break;
        }
    }


    public String GetIndexMes(int mesString){
        String mesCurrent = "";
        switch (mesString){
            case 0:
                mesCurrent = "Enero";
                break;
            case 1:
                mesCurrent = "Febrero";
                break;
            case 2:
                mesCurrent = "Marzo";
                break;
            case 3:
                mesCurrent = "Abril";
                break;
            case 4:
                mesCurrent = "Mayo";
                break;
            case 5:
                mesCurrent = "Junio";
                break;
            case 6:
                mesCurrent = "Julio";
                break;
            case 7:
                mesCurrent = "Agosto";
                break;
            case 8:
                mesCurrent = "Septiembre";
                break;
            case 9:
                mesCurrent = "Octubre";
                break;
            case 10:
                mesCurrent = "Noviembre";
                break;
            case 11:
                mesCurrent = "Diciembre";
                break;
        }
        return mesCurrent;
    }
    public int getToday() {
        return today;
    }

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

    public TramitesBusiness getTramitesBusiness() {
        return tramitesBusiness;
    }

    public void setTramitesBusiness(TramitesBusiness tramitesBusiness) {
        this.tramitesBusiness = tramitesBusiness;
    }

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

    /**/
    public CitaBusiness getCitaBusiness() {
        return citaBusiness;
    }

    public void setCitaBusiness(CitaBusiness citaBusiness) {
        this.citaBusiness = citaBusiness;
    }

    public String getStrIdArea() {
        return strIdArea;
    }

    public List<String> getListDaysToCheck() {
        return listDaysToCheck;
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

    public void setToday(int today) {
        this.today = today;
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
    public String getStrDia() {
        return strDia;
    }

    public void setStrDia(String strDia) {
        this.strDia = strDia;
    }

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

    public String getAnoActual() {
        return anoActual;
    }

    public void setAnoActual(String anoActual) {
        this.anoActual = anoActual;
    }

}
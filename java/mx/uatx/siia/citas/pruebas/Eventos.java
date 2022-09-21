package mx.uatx.siia.citas.pruebas;

import net.bootsfaces.component.fullCalendar.FullCalendarEventBean;

import java.util.Date;

public class Eventos extends FullCalendarEventBean {

    @Override
    public void setUrl(String url) {
        super.setUrl(url);
    }

    public Eventos(String title, Date start, String url) {
        super(title, start);
        setUrl(url);
    }

    @Override
    public void addExtendedFields(StringBuilder buf) {
        buf.append("'url:google.com'");
    }
}

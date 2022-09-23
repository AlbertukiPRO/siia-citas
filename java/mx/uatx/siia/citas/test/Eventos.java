package mx.uatx.siia.citas.test;

import net.bootsfaces.component.fullCalendar.FullCalendarEventBean;

import java.util.Date;

public class Eventos extends FullCalendarEventBean {

    public Eventos(String title, Date start) {
        super(title, start);
    }

    @Override
    public void addExtendedFields(StringBuilder buf) {

    }
}

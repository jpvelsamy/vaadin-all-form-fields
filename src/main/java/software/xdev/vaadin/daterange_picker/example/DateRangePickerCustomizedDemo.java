package software.xdev.vaadin.daterange_picker.example;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import software.xdev.vaadin.daterange_picker.business.DateRangeModel;
import software.xdev.vaadin.daterange_picker.example.customized.CustomDateRange;
import software.xdev.vaadin.daterange_picker.example.customized.CustomDateRanges;
import software.xdev.vaadin.daterange_picker.ui.DateRangePicker;


@Route(value = "date-1", layout = MainView.class)
@PageTitle("Date 1")
public class DateRangePickerCustomizedDemo extends Div
{
	
	
	protected static final List<CustomDateRange> DATERANGE_VALUES = Arrays.asList(CustomDateRanges.allValues());
	
	private final DateRangePicker<CustomDateRange> dateRangePicker =
		new DateRangePicker<>(
			() -> new DateRangeModel<>(LocalDate.now(), LocalDate.now(), CustomDateRanges.DAY),
			DATERANGE_VALUES);
	
	private final TextArea taResult =
		new TextArea("ValueChangeEvent", "Change something in the datepicker to see the result");
	
	/*
	 * Fields
	 */
	
	public DateRangePickerCustomizedDemo()
	{
		this.initUI();
	}
	
	protected void initUI()
	{
		this.taResult.setSizeFull();
		add(this.dateRangePicker, this.taResult);
		
		this.dateRangePicker.addValueChangeListener(ev ->
		{
			final DateRangeModel<CustomDateRange> modell = ev.getValue();
			
			this.taResult.clear();
			// @formatter:off
			this.taResult.setValue(
					"DateRange: " + modell.getDateRange().getKey() + "\r\n" +
					"DateRange-Tag: " + modell.getDateRange().getTag() + "\r\n" +
					"Start: " + modell.getStart() + "\r\n" +
					"End: " + modell.getEnd()
				);
			// @formatter:on
		});
	}
}

package com.example.application.views.personform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.IOUtils;
import org.vaadin.miki.superfields.buttons.MultiClickButton;
import org.vaadin.miki.superfields.buttons.SimpleButtonState;
import org.vaadin.miki.superfields.dates.SuperDatePicker;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.text.SuperTextArea;
import org.vaadin.miki.superfields.text.SuperTextField;

import com.example.application.data.entity.SamplePerson;
import com.example.application.data.service.SamplePersonService;
import com.example.application.views.main.MainView;
import com.flowingcode.vaadin.addons.chipfield.ChipField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.internal.MessageDigestUtil;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;

import de.codecamp.vaadin.components.messagedialog.MessageDialog;
import de.codecamp.vaadin.components.messagedialog.MessageDialog.FluentButton;
import software.xdev.vaadin.chips.ChipComboBox;
import software.xdev.vaadin.daterange_picker.business.DateRangeModel;
import software.xdev.vaadin.daterange_picker.example.customized.CustomDateRange;
import software.xdev.vaadin.daterange_picker.example.customized.CustomDateRanges;
import software.xdev.vaadin.daterange_picker.ui.DateRangePicker;

@Route(value = "person-form", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Person Form")
public class PersonFormView extends Div {

	private SuperTextField firstName = new SuperTextField("First name");
	private SuperTextField lastName = new SuperTextField("Last name");
	private EmailField email = new EmailField("Email address");
	private SuperDatePicker dateOfBirth = new SuperDatePicker("Birthday");
	private PhoneNumberField phone = new PhoneNumberField("Phone number");
	private SuperTextField occupation = new SuperTextField("Occupation");
	private SuperIntegerField age = new SuperIntegerField("Age");

	private Button cancel = new Button("Cancel");
	private Button save = new Button("Save");
	private ChipField<String> chf = new ChipField<>("Select some planets", "Mercury", "Venus", "Earth", "Mars",
			"Jupiter", "Saturn", "Uranus", "Neptune");
	protected static final List<CustomDateRange> DATERANGE_VALUES = Arrays.asList(CustomDateRanges.allValues());
	private final DateRangePicker<CustomDateRange> dateRangePicker = new DateRangePicker<>(
			() -> new DateRangeModel<>(LocalDate.now(), LocalDate.now(), CustomDateRanges.DAY), DATERANGE_VALUES);
	private final SuperTextArea taResult = new SuperTextArea("ValueChangeEvent",
			"Change something in the datepicker to see the result");
	private final boolean useLanguageCookies = true;
	// private final LanguageSelect langSelect = new
	// LanguageSelect(useLanguageCookies, new Locale("de"), new Locale("fr"), new
	// Locale("en"), new Locale("in"));
	private final ChipComboBox<String> comboBox = new ChipComboBox<String>().withLabel("Programming languages")
			.withPlaceholder("Select programming language").withFullComboBoxWidth();
	
	private final Accordion accordion = buildAccordion();
	//private final ProgressBar progressBar = new ProgressBar(10, 100, 20);

	private Binder<SamplePerson> binder = new Binder(SamplePerson.class);

	public PersonFormView(SamplePersonService personService) {
		addClassName("person-form-view");

		add(createTitle());
		add(createFormLayout());
		add(createButtonLayout());
		add(accordion);
		binder.bindInstanceFields(this);
		clearForm();

		cancel.addClickListener(e -> clearForm());
		save.addClickListener(e -> {
			personService.update(binder.getBean());
			Notification.show(binder.getBean().getClass().getSimpleName() + " details stored.");
			clearForm();
		});
		this.taResult.setSizeFull();
		this.dateRangePicker.addValueChangeListener(ev -> {
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
		this.comboBox.withAllAvailableItems(
				Arrays.asList("Java", "TypeScript", "Shell", "JavaScript", "Kotlin", "C#", "Phyton"));

		this.comboBox.addValueChangeListener(ev ->
		// @formatter:off
        {
        	this.taResult.clear();
			this.taResult.setValue(
					"Value: [" + ev.getValue().stream().collect(Collectors.joining(", ")) + "] \r\n" +
					"OldValue: [" + ev.getOldValue().stream().collect(Collectors.joining(", ")) + "] \r\n" +
					"IsFromClient: " + ev.isFromClient()
			);
		// @formatter:on
		});
		
	}

	private void clearForm() {
		MessageDialog dialog = new MessageDialog();
		dialog.setTitle("Error", VaadinIcon.WARNING.create());
		dialog.setMessage("Unfortunately, things didn't go as planned.");

		dialog.addButtonToLeft().text("Details").title("Tooltip").icon(VaadinIcon.ARROW_DOWN).toggleDetails();
		FluentButton clear = dialog.addButtonToLeft().text("Abort").tertiary().closeOnClick();
		dialog.addButton().text("Ignore").error().closeOnClick();
		dialog.addButton().text("Retry").icon(VaadinIcon.ROTATE_LEFT).primary().closeOnClick();

		TextArea detailsText = new TextArea();
		detailsText.setWidthFull();
		detailsText.setMaxHeight("15em");
		detailsText.setReadOnly(true);
		detailsText.setValue(
				"Lengthy, technical error details.\nLengthy, technical error details.\nLengthy, technical error details.\nLengthy, technical error details.");
		dialog.getDetails().add(detailsText);

		dialog.open();
		clear.getButton().addClickListener(event -> {
			binder.setBean(new SamplePerson());
		});

	}

	private Component createTitle() {
		return new H3("Personal information");
	}

	private Component createFormLayout() {
		FormLayout formLayout = new FormLayout();
		email.setErrorMessage("Please enter a valid email address");
		formLayout.add(firstName, lastName, comboBox, dateOfBirth, phone, email, occupation, age, chf, dateRangePicker,
				taResult);
		return formLayout;
	}

	private Component createButtonLayout() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addClassName("button-layout");
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		buttonLayout.add(cancel);
		buttonLayout.add(save);

		return buttonLayout;
	}

	private static class PhoneNumberField extends CustomField<String> {
		private ComboBox<String> countryCode = new ComboBox<>();
		private TextField number = new TextField();

		public PhoneNumberField(String label) {
			setLabel(label);
			countryCode.setWidth("120px");
			countryCode.setPlaceholder("Country");
			countryCode.setPattern("\\+\\d*");
			countryCode.setPreventInvalidInput(true);
			countryCode.setItems("+354", "+91", "+62", "+98", "+964", "+353", "+44", "+972", "+39", "+225");
			countryCode.addCustomValueSetListener(e -> countryCode.setValue(e.getDetail()));
			number.setPattern("\\d*");
			number.setPreventInvalidInput(true);
			HorizontalLayout layout = new HorizontalLayout(countryCode, number);
			layout.setFlexGrow(1.0, number);
			add(layout);
		}

		@Override
		protected String generateModelValue() {
			if (countryCode.getValue() != null && number.getValue() != null) {
				String s = countryCode.getValue() + " " + number.getValue();
				return s;
			}
			return "";
		}

		@Override
		protected void setPresentationValue(String phoneNumber) {
			String[] parts = phoneNumber != null ? phoneNumber.split(" ", 2) : new String[0];
			if (parts.length == 1) {
				countryCode.clear();
				number.setValue(parts[0]);
			} else if (parts.length == 2) {
				countryCode.setValue(parts[0]);
				number.setValue(parts[1]);
			} else {
				countryCode.clear();
				number.clear();
			}
		}
	}
	
	private Accordion buildAccordion() {
		// BEGIN ACCORDION
		Accordion accordion = new Accordion();

		// ACCOUNT INFORMATION
		FormLayout accountForm = new FormLayout();
		accountForm.add(new TextField("Email"));
		accountForm.add(new TextField("Handle"));
		accountForm.add(new PasswordField("Password"));
		accountForm.add(new PasswordField("Confirm password"));

		accordion.add("Account information", accountForm);

		// PROFILE INFORMATION
		FormLayout profileInfoForm = new FormLayout();
		profileInfoForm.add(new TextField("First name"));
		profileInfoForm.add(new TextField("Last name"));
		RadioButtonGroup<String> languageGroup = new RadioButtonGroup<>();
		languageGroup.setLabel("Language");
		languageGroup.setItems("English", "Finnish");
		profileInfoForm.add(languageGroup);
		profileInfoForm.add(new SuperDatePicker("Date of birth"));
		NumberField numberField = new NumberField("Age");
		numberField.setValue(1d);
		numberField.setHasControls(true);
		numberField.setMin(1);
		numberField.setMax(10);
		profileInfoForm.add(numberField);

		accordion.add("Profile information", profileInfoForm);

		// TOPICS OF INTEREST
		FormLayout topicsForm = new FormLayout();
		topicsForm.add(new Checkbox("Culture"));
		topicsForm.add(new Checkbox("Environment"));
		topicsForm.add(new Checkbox("Fashion"));
		topicsForm.add(new Checkbox("Finance"));
		topicsForm.add(new Checkbox("Food", true));
		topicsForm.add(new Checkbox("Politics"));
		topicsForm.add(new Checkbox("Sports"));
		topicsForm.add(new Checkbox("Technology", true));

		accordion.add("Topics of interest", topicsForm);

		// TERMS AND CONDITIONS
		Paragraph paragraph = new Paragraph();
		paragraph.setText("After all has been said and done, I agree that "
		        + "my data shall be safely stored for the sole purpose of "
		        + "my ultimate enjoyment.");
		
		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
		Div output = new Div();

		upload.addSucceededListener(event -> {
		    Component component = createComponent(event.getMIMEType(),
		            event.getFileName(),
		            buffer.getInputStream(event.getFileName()));
		    showOutput(event.getFileName(), component, output);
		});
		upload.addFileRejectedListener(event -> {
		    Paragraph component = new Paragraph();
		    showOutput(event.getErrorMessage(), component, output);
		});

		topicsForm.add(upload, output);

		//Button submit = new Button("Sign up");
		
		MultiClickButton submit = new MultiClickButton(event -> UI.getCurrent().navigate(""),
				new SimpleButtonState("Save").withThemeVariant(ButtonVariant.LUMO_PRIMARY),
				new SimpleButtonState("Are you sure?", VaadinIcon.INFO_CIRCLE.create()),
				new SimpleButtonState("Confirm ?", VaadinIcon.INFO.create())
						.withThemeVariant(ButtonVariant.LUMO_ERROR)).withId("multi-click-button");
		submit.setEnabled(false);
		submit.addClickListener(e -> Notification.show("Complete! \uD83D\uDC4D",
		        4000, Notification.Position.BOTTOM_END));
		Checkbox consent = new Checkbox("I agree");
		consent.addValueChangeListener(e -> submit.setEnabled(e.getValue()));

		HorizontalLayout bottomPanel = new HorizontalLayout(consent, submit);
		bottomPanel.setWidthFull();
		bottomPanel.setFlexGrow(1, consent);
		VerticalLayout terms = new VerticalLayout(paragraph, bottomPanel);

		accordion.add("Terms and conditions", terms);
		return accordion;
	}
	
	private Component createComponent(String mimeType, String fileName,
	        InputStream stream) {
	    if (mimeType.startsWith("text")) {
	        return createTextComponent(stream);
	    } else if (mimeType.startsWith("image")) {
	        Image image = new Image();
	        try {

	            byte[] bytes = IOUtils.toByteArray(stream);
	            image.getElement().setAttribute("src", new StreamResource(
	                    fileName, () -> new ByteArrayInputStream(bytes)));
	            try (ImageInputStream in = ImageIO.createImageInputStream(
	                    new ByteArrayInputStream(bytes))) {
	                final Iterator<ImageReader> readers = ImageIO
	                        .getImageReaders(in);
	                if (readers.hasNext()) {
	                    ImageReader reader = readers.next();
	                    try {
	                        reader.setInput(in);
	                        image.setWidth(reader.getWidth(0) + "px");
	                        image.setHeight(reader.getHeight(0) + "px");
	                    } finally {
	                        reader.dispose();
	                    }
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        image.setSizeFull();
	        return image;
	    }
	    Div content = new Div();
	    String text = String.format("Mime type: '%s'\nSHA-256 hash: '%s'",
	            mimeType, MessageDigestUtil.sha256(stream.toString()));
	    content.setText(text);
	    return content;

	}

	private Component createTextComponent(InputStream stream) {
	    String text;
	    try {
	        text = IOUtils.toString(stream, StandardCharsets.UTF_8);
	    } catch (IOException e) {
	        text = "exception reading stream";
	    }
	    return new Text(text);
	}

	private void showOutput(String text, Component content,
	        HasComponents outputContainer) {
	    HtmlComponent p = new HtmlComponent(Tag.P);
	    p.getElement().setText(text);
	    outputContainer.add(p);
	    outputContainer.add(content);
	}

}

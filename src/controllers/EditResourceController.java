package controllers;

import java.io.File;
import java.util.ArrayList;

import data.*;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controller for the Edit Resources page.
 * Allows librarians to edit the details of existing resources.
 * @author William King
 */
public class EditResourceController {
	/** The directory to the thumbnail images for the resources. */
	private final String RESOURCE_IMAGE_PATH = "DataFiles/ResourceThumbnails/";
	/** An array list that holds all the existing books. */
	private ArrayList<Book> bookList;
	/** An array list that holds all the existing DVDs. */
	private ArrayList<DVD> dvdList;
	/** An array list that holds all the existing laptops.*/
	private ArrayList<Laptop> laptopList;
	/** Keeps track of the current languages in the list view */
	private ArrayList<String> currentLangList =  new ArrayList<String>();
	
	/** Holds the files of all the resource thumbnail images. */
	private File[] resourceImageList;
	
	/** Local storage of the resource being edited. */
	private Resource editedResource;
	
	/** A combo box used to select an image for a resource. */
	@FXML private ComboBox<String> cmbResourceImage;
	/** A list view used to display the DVD's subtitle languages. */
	@FXML private ListView<String>  lstSubLang;
	/** The add button for the subtitle languages list view. */
	@FXML private Button btnAdd;
	/** The remove button for the subtitle languages list view.. */
	@FXML private Button btnRemove;	
	/** The back button for the page. */
	@FXML private Button btnBack;
	
	/** An image view to hold the resource thumbnail. */
	@FXML private ImageView imageThumbnail;
	
	/** A text field used to hold the resource's title. */
	@FXML private TextField txtResourceTitle;
	/** A text field used to hold the year the resource was released. */
	@FXML private TextField txtYear;
	/** A text field used to hold the language of the book / DVD. */
	@FXML private TextField txtLanguage;
	
	/** A text field used to hold the author of the book. */
	@FXML private TextField txtAuthor;
	/** A text field used to hold the book's publisher. */
	@FXML private TextField txtPublisher;
	/** A text field used to hold the genre of the book. */
	@FXML private TextField txtGenre;
	/**  A text field used to hold the book's ISBN. */
	@FXML private TextField txtISBN;
	
	/** A text field used to hold the director of the DVD. */
	@FXML private TextField txtDirector;
	/** A text field used to hold the DVD's runtime. */
	@FXML private TextField txtRuntime;
	/** A text field used to hold the DVD's subtitle language. */
	@FXML private TextField txtSubLang;
	
	/** A text field used to hold the laptop's manufacturer. */
	@FXML private TextField txtManufacturer;
	/** A text field used to hold the laptop's model. */
	@FXML private TextField txtModel;
	/** A text field used to hold the laptop's operating system. */
	@FXML private TextField txtOperatingSystem;
	
	/**
	 * Sets up the image thumbnail combo box.
	 */
	public void initialize() {
		//Creates an array of all resource images.
		File folder = new File(RESOURCE_IMAGE_PATH);
		resourceImageList = folder.listFiles();

		for (File file : resourceImageList) {
			if (file.isFile()) {
				cmbResourceImage.getItems().add(file.getName());
			}
		}
	}
	
	/**
	 * Displays the current details of the resource to be edited in the 
	 * appropriate text fields.
	 * @param editedResource The resource to be edited.
	 */
	public void editResource(Resource editedResource) {
		// Keeps local storage of the edited resource.
		this.editedResource = editedResource;
		String resourceType = editedResource.getType();
		
		// Displays the resource's editable details on screen in the
		// appropriate text fields.
		txtResourceTitle.setText(editedResource.getResourceTitle());
		txtYear.setText(editedResource.getYear() + "");
		
		File imageURL = new File(RESOURCE_IMAGE_PATH 
				+ editedResource.getThumbnail());
		Image resourceImage = new Image(imageURL.toURI().toString());
		imageThumbnail.setImage(resourceImage);
		
		switch (resourceType) {
			case "Book":
				setBookFields((Book) editedResource);
				break;
			case "DVD":
				setDVDFields((DVD) editedResource);
				break;
			case "Laptop":
				setLaptopFields((Laptop) editedResource);
		}
	}
	
	/**
	 * Validates the changes made to the edited resource and saves them
	 * if they are valid.
	 */
	public void handleSaveButtonAction() {
		String resourceType = editedResource.getType();
		switch (resourceType) {
			case "Book":
				validateEditedBook();
				break;
			case "DVD":
				validateEditedDVD();
				break;
			case "Laptop":
				validateEditedLaptop();
				break;
		}
	}
	
	/**
	 * Adds the inputted subtitle language from the text box
	 * into the list view.
	 */
	public void handleAddLanguageButtonAction() {
		String language = txtSubLang.getText().trim();
		// Checks if there's no duplicate languages.
		boolean languageExist;
		boolean hasLetter = Utility.isAlphaLanguage(language);
		 
		if (language.length() == 0) {
			Alerts.languageNotEntered();
			return;
		} else if (!hasLetter) {
			Alerts.nonAlphaError();
			return;
		}
		
		languageExist = currentLangList.contains(language);
		if (languageExist) {
			Alerts.languageExists();
			return;
		}
		lstSubLang.getItems().add(language);
		currentLangList.add(language);
		txtSubLang.clear();
	}
	
	/**
	 * Removes a selected subtitle language from the list view.
	 */
	public void handleRemoveLanguageButtonAction() {
		int selectedIndex = lstSubLang.getSelectionModel().getSelectedIndex();
		
		if (currentLangList.size() == 0) {
			Alerts.languageListEmpty();
			return;
		} else if (selectedIndex < 0) {
			Alerts.languageNotSelected();
			return;
		}
		lstSubLang.getItems().remove(selectedIndex);
		currentLangList.remove(selectedIndex);
	}
	
	/**
	 * Validates the changes made to the edited book.
	 */
	public void validateEditedBook() {
		String resourceTitle = txtResourceTitle.getText().trim();
		String strYear = txtYear.getText().trim(); 
		String author = txtAuthor.getText().trim();
		String publisher = txtPublisher.getText().trim();
		String genre = txtGenre.getText().trim();
		String language = txtLanguage.getText().trim();
		String isbn = txtISBN.getText().trim();
		String imageName;
		
		// Gets the position of the selected resource image.
		int selectedIndex = cmbResourceImage.getSelectionModel()
				.getSelectedIndex();
		
		// Sets a new resource image if it has been selected.
		if (selectedIndex > -1) {
			imageName = resourceImageList[selectedIndex].getName();
		} else {
			// Sets to the previous resource image.
			imageName = editedResource.getThumbnail();
		}
		
		//Validation applied to the inputed values.
		boolean bookFieldsFilled = Utility.isBookFieldFilled(resourceTitle, 
				strYear, author, publisher); 
		boolean isNum = Utility.isInt(strYear) && Utility.isInt(isbn);
		boolean isAlpha = Utility.isAlphaBook(author, publisher, 
				genre, language);
		
		if (!bookFieldsFilled) {
			Alerts.missingFields();
			return;
		} else if (!isNum) {
			Alerts.nonIntegerError();
			return;
		} else if (!isAlpha) {
			Alerts.nonAlphaError();
			return;
		}
		
		// It's assumed that year is an integer.
		int year = Integer.parseInt(strYear);
		
		boolean bookExists = Utility.isBookExist(resourceTitle, year,
				imageName, author, publisher, genre, 
				language, isbn, bookList);
		
		// Checks if the entered details match with an existing book.
		if (bookExists) {
			Alerts.resourceExistsEdit();
			return;
		}
		
		Book editedBook = (Book) editedResource;
		String oldBook = editedBook.toStringDetail();
		
		editedBook.setResourceTitle(resourceTitle);
		editedBook.setYear(year);
		editedBook.setThumbnail(imageName);
		editedBook.setAuthor(author);
		editedBook.setPublisher(publisher);
		editedBook.setGenre(genre);
		editedBook.setISBN(isbn);
		editedBook.setLanguage(language);
		
		String newBook = editedBook.toStringDetail();
		FileHandling.editResource(oldBook, newBook, "Book");
		handleBackButtonAction();
	}
	
	/**
	 * Validates the changes made to the edited DVD.
	 */
	public void validateEditedDVD() {
		String resourceTitle = txtResourceTitle.getText().trim();
		String strYear = txtYear.getText().trim(); 
		String[] subLang = currentLangList.toArray(
				new String[currentLangList.size()]);
		String director = txtDirector.getText().trim();
		String strRuntime = txtRuntime.getText().trim();
		String language = txtLanguage.getText().trim();
		String imageName;
		
		//Gets the position of the selected resource image.
		int selectedIndex = cmbResourceImage.getSelectionModel()
				.getSelectedIndex();
		
		//Sets a new resource image if it has been selected.
		if (selectedIndex > -1) {
			imageName = resourceImageList[selectedIndex].getName();
		} else {
			//Sets to the previous resource image.
			imageName = editedResource.getThumbnail();
		}
		
		//Validation applied to the inputed values.
		boolean dvdFieldsFilled = Utility.isDVDFieldFilled(resourceTitle, 
				strYear, director, strRuntime);
		boolean isNum = Utility.isInt(strYear);
		boolean isDouble = Utility.isDouble(strRuntime);
		boolean isAlpha = Utility.isAlphaDVD(director, language);
		
		if (!dvdFieldsFilled) {
			Alerts.missingFields();
			return;
		} else if (!isNum) {
			Alerts.nonIntegerError();
			return;
		} else if (!isDouble) {
			Alerts.nonDoubleError();
			return;
		} else if (!isAlpha) {
			Alerts.nonAlphaError();
			return;
		}
		
		// It's assumed that year is an integer and that runtime is a double.
		int year = Integer.parseInt(strYear);
		double runtime = Double.parseDouble(strRuntime);
		boolean dvdExists = Utility.isDVDExist(resourceTitle, year, 
				imageName, director, runtime, subLang, language, 
				dvdList);
		
		// Checks if the entered details match with an existing DVD.
		if (dvdExists) {
			Alerts.resourceExistsEdit();
			return;
		}
		
		DVD editedDVD = (DVD) editedResource;
		String oldDVD = editedDVD.toStringDetail();
		
		editedDVD.setResourceTitle(resourceTitle);
		editedDVD.setYear(year);
		editedDVD.setThumbnail(imageName);
		editedDVD.setDirector(director);
		editedDVD.setRuntime(runtime);
		editedDVD.setLanguage(language);
		editedDVD.setSubLang(subLang);
		
		String newDVD = editedDVD.toStringDetail();
		FileHandling.editResource(oldDVD, newDVD, "DVD");
		handleBackButtonAction();
	}
	
	/**
	 * Validates the changes made to the edited laptop.
	 */
	public void validateEditedLaptop() {
		String resourceTitle = txtResourceTitle.getText().trim();
		String strYear = txtYear.getText().trim();
		String manufacturer = txtManufacturer.getText().trim();
		String model = txtModel.getText().trim();
		String operatingSystem = txtOperatingSystem.getText().trim();
		String imageName;
		
		//Gets the position of the selected resource image.
		int selectedIndex = cmbResourceImage.getSelectionModel()
				.getSelectedIndex();
		
		//Sets a new resource image if it has been selected.
		if (selectedIndex > -1) {
			imageName = resourceImageList[selectedIndex].getName();
		} else {
			//Sets to the previous resource image.
			imageName = editedResource.getThumbnail();
		}
		
		//Validation applied to the inputted values.
		boolean laptopFieldsFilled = Utility.isLaptopFieldFilled(resourceTitle, 
				strYear, manufacturer, model, operatingSystem);
		boolean isNum = Utility.isInt(strYear);
		boolean isAlpha = Utility.isAlphaLaptop(resourceTitle, operatingSystem, 
				model, manufacturer);
		
		if (!laptopFieldsFilled) {
			Alerts.missingFields();
			return;
		} else if (!isNum) {
			Alerts.nonIntegerError();
			return;
		} else if (!isAlpha) {
			Alerts.nonAlphaError();
			return;
		}
		
		// It's assumed that year is an integer.
		int year = Integer.parseInt(strYear);
		
		boolean laptopExists = Utility.isLaptopExist(resourceTitle, year, 
				imageName, manufacturer, model, operatingSystem, laptopList);
		
		// Checks if the entered details match with an existing laptop.
		if (laptopExists) {
			Alerts.resourceExistsEdit();
			return;
		}
		
		Laptop editedLaptop = (Laptop) editedResource;
		String oldLaptop = editedLaptop.toStringDetail();
		
		editedLaptop.setResourceTitle(resourceTitle);
		editedLaptop.setYear(year);
		editedLaptop.setThumbnail(imageName);
		editedLaptop.setManufacturer(manufacturer);
		editedLaptop.setModel(model);
		editedLaptop.setOperatingSystem(operatingSystem);
		
		String newLaptop = editedLaptop.toStringDetail();
		FileHandling.editResource(oldLaptop, newLaptop, "Laptop");
		handleBackButtonAction();
	}
	
	/**
	 * Allows the librarian to select a resource thumbnail and
	 * displays it onto the screen.
	 */
	public void handleResourceThumbnailComboBoxAction() {
		//Gets the position of the selected thumbnail.
		int selectedIndex = cmbResourceImage.getSelectionModel()
				.getSelectedIndex();
		
		File imageURL = resourceImageList[selectedIndex];
		Image profilePicture = new Image(imageURL.toURI().toString());
		imageThumbnail.setImage(profilePicture);
	}
	
	/**
	 * Sets the text fields for the edited book.
	 */
	public void setBookFields(Book editedBook) {
		txtLanguage.setVisible(true);
		
		txtAuthor.setVisible(true);
		txtPublisher.setVisible(true);
		txtGenre.setVisible(true);
		txtISBN.setVisible(true);
		
		txtDirector.setVisible(false);
		txtRuntime.setVisible(false);
		txtSubLang.setVisible(false);
		lstSubLang.setVisible(false);
		btnAdd.setVisible(false);
		btnRemove.setVisible(false);
		
		txtManufacturer.setVisible(false);
		txtModel.setVisible(false);
		txtOperatingSystem.setVisible(false);
		
		txtAuthor.setText(editedBook.getAuthor());
		txtPublisher.setText(editedBook.getPublisher());
		txtLanguage.setText(editedBook.getLanguage());
		txtISBN.setText(editedBook.getISBN());
		txtGenre.setText(editedBook.getGenre());
	}
	
	
	/**
	 * Sets the text fields for the edited DVD.
	 */
	public void setDVDFields(DVD editedDVD) {
		txtLanguage.setVisible(true);
		
		txtAuthor.setVisible(false);
		txtPublisher.setVisible(false);
		txtGenre.setVisible(false);
		txtISBN.setVisible(false);
		
		txtDirector.setVisible(true);
		txtRuntime.setVisible(true);
		txtSubLang.setVisible(true);
		lstSubLang.setVisible(true);
		btnAdd.setVisible(true);
		btnRemove.setVisible(true);
		
		txtManufacturer.setVisible(false);
		txtModel.setVisible(false);
		txtOperatingSystem.setVisible(false);
		
		txtDirector.setText(editedDVD.getDirector());
		txtRuntime.setText(editedDVD.getRuntime() + "");
		txtLanguage.setText(editedDVD.getLanguage());
		lstSubLang.getItems().clear();
		
		String[] subLang = editedDVD.getSubLang();
		for (String lang : subLang) {
			lstSubLang.getItems().add(lang);
			currentLangList.add(lang);
		}
	}
	
	/**
	 * Sets the text fields for the edited laptop.
	 */
	public void setLaptopFields(Laptop editedLaptop) {
		txtLanguage.setVisible(false);
		
		txtAuthor.setVisible(false);
		txtPublisher.setVisible(false);
		txtGenre.setVisible(false);
		txtISBN.setVisible(false);
		
		txtDirector.setVisible(false);
		txtRuntime.setVisible(false);
		txtSubLang.setVisible(false);
		lstSubLang.setVisible(false);
		btnAdd.setVisible(false);
		btnRemove.setVisible(false);
		
		txtManufacturer.setVisible(true);
		txtModel.setVisible(true);
		txtOperatingSystem.setVisible(true); 
		
		txtManufacturer.setText(editedLaptop.getManufacturer());
		txtModel.setText(editedLaptop.getModel());
		txtOperatingSystem.setText(editedLaptop.getOperatingSystem());
	}
	
	/**
	 * Sets the array lists for each resource.
	 * Used to check if the edited resource's details matches with 
	 * an existing resource.
	 * @param bookList The ArrayList of all current books.
	 * @param dvdList The ArrayList of all current DVDs.
	 * @param laptopList The ArrayList of all current laptops.
	 */
	public void setResourceArrays(ArrayList<Book> bookList, 
			ArrayList<DVD> dvdList, ArrayList<Laptop> laptopList) {
		this.bookList = bookList;
		this.dvdList = dvdList;
		this.laptopList = laptopList;
	}
	
	/**
	 * Gets the resource being edited.
	 * @return The resource being edited.
	 */
	public Resource getEditedResource() {
		return editedResource;
	}
	
	/**
	 * Closes the current page.
	 */
	public void handleBackButtonAction() {
		Stage curStage = (Stage) btnBack.getScene().getWindow(); 
		curStage.close(); 
	}
}
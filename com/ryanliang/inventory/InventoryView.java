/**
 *
 * @author Ryan Liang
 */

package com.ryanliang.inventory;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InventoryView implements Viewable {
	private static Scanner sc = new Scanner(System.in);
	private final Controllable controller;
	private Modellable model;
	
	private static String itemID = "0";


	public InventoryView(Controllable controller) {
		super();
		this.controller = controller;
	}

	public void start() {
		controller.loadData();
		boolean run = true;
		int input = 5;
	
		while (run){
			boolean continueInput = true;
	
			do {
				try{
					System.out.println("Media (CD, DVD or Book) inventory system");
					System.out.println("1 - Add new item");
					System.out.println("2 - Search item");
					System.out.println("3 - Edit item");
					System.out.println("4 - Delete item");
					System.out.println("5 - Quit");
					input = sc.nextInt();
					
					//To absorb the \n of the input.
					sc.nextLine();

					continueInput = false;
				}
				catch (InputMismatchException ex) {
					System.out.println("Try again. (" + "Incorrect input: an integer number is required)");
					sc.nextLine();
				}
			}
			while (continueInput);

			if (input == 1){
				newItem();
			}

			else if (input == 2){
				searchItem();
			}
			
			else if (input == 3){
				editItem();
			}
			
			else if (input == 4){
				deleteItem();
			}

			else if (input == 5){
				controller.saveData();
				run = false; 
			}		
		}

		sc.close();
	}

	private void editItem() {
		
		System.out.println("Enter item ID to edit: ");
		String itemID = sc.nextLine().trim();
		
		System.out.println();
		controller.searchItemForEditing(itemID);
	}

	private void deleteItem() {

		System.out.println("Enter item ID to delete: ");
		String itemID = sc.nextLine().trim();
		
		System.out.println();
		controller.deleteItem(itemID);
		
		System.out.println("Item is deleted\n");
		
	}

	private void searchItem() {
		
		System.out.println("Enter item ID number or a single phrase: ");
		String query = sc.nextLine().trim();
		
		System.out.println();
		if (query.toLowerCase().equals("none") || query.toLowerCase().equals("non") || query.toLowerCase().equals("no") || query.toLowerCase().equals("ne") || query.toLowerCase().equals("one") || query.toLowerCase().equals("on"))
			System.out.println("Search phrase can not be accepted.  Try again.\n");
		else{
			System.out.print("Search result: ");
			controller.searchItem(query);
		}
		
	}

	private void newItem() {
		String none = "None";
		MediaCategory media = getMediaInput();

		System.out.println("Please enter the following information: \n");
		
		System.out.println("Title: ");
		String title = sc.nextLine().trim();
		System.out.println("Quantity: ");
		String quantity = sc.nextLine().trim();
		System.out.println("Genre: ");
		String genre = sc.nextLine().trim();
		System.out.println("Description: ");
		String description = sc.nextLine().trim();
		
		title = title.equals("")?none:title;
		quantity = quantity.equals("")?"1":quantity;
		
		if (!Utility.isNumeric(quantity))
			quantity = "1";
			
		genre = genre.equals("")?none:genre;
		description = description.equals("")?none:description;
		
		controller.generateID();
		
		if (media == MediaCategory.CD){
			System.out.println("Artist(s): ");
			String artist = sc.nextLine().trim();
			
			artist = artist.equals("")?none:artist;
			
			if (title.equals(none) && description.equals(none) && artist.equals(none))
				System.out.println("Item can not be accepted due to missing certain user input(s)\n");
			else{ 
				controller.addItem(new CD(itemID, title, description , genre, artist), quantity); 
				controller.searchItem(itemID);
			}
		}
		else if (media == MediaCategory.DVD){
			System.out.println("Cast(s): ");
			String cast = sc.nextLine().trim();
			
			cast = cast.equals("")?none:cast;
			
			if (title.equals(none) && description.equals(none) && cast.equals(none))
				System.out.println("Item can not be accepted due to missing certain user input(s)\n");
			else{ 
				controller.addItem(new DVD(itemID, title,description , genre, cast), quantity); 
				controller.searchItem(itemID);
			}
		}
		else if (media == MediaCategory.BOOK){
			System.out.println("Author(s): ");
			String author = sc.nextLine().trim();
			System.out.println("ISBN: ");
			String ISBN = sc.nextLine().trim();
			
			author = author.equals("")?none:author;
			ISBN = ISBN.equals("")?none:ISBN;
			
			if (title.equals(none) && description.equals(none) && author.equals(none) && ISBN.equals(none))
				System.out.println("Item can not be accepted due to missing certain user input(s)\n");
			else{ 
				controller.addItem(new Book(itemID, title, description , genre, author, ISBN), quantity);  
				controller.searchItem(itemID);
			}
		}	

	}

	private MediaCategory getMediaInput() {
		boolean run = true;
		int input = 1;
	
		while (run){
			boolean continueInput = true;
	
			do {
				try{
					System.out.println("1 - CD");
					System.out.println("2 - DVD");
					System.out.println("3 - Book");

					input = sc.nextInt();
					
					//To absorb the \n of the input.
					sc.nextLine();

					continueInput = false;
				}
				catch (InputMismatchException ex) {
					System.out.println("Try again. (" + "Incorrect input: an integer number is required)");
					sc.nextLine();
				}
			}
			while (continueInput);

			if (input == 1){
				return MediaCategory.CD;
			}

			else if (input == 2){
				return MediaCategory.DVD;
			}
			
			else if (input == 3){
				return MediaCategory.BOOK;
			}
		}
		//Needed default return statement to avoid compiler error but will never get executed
		return MediaCategory.CD;
	}

	public void setModel(Modellable model) {
		this.model = model;
		
	}

	public void update(UpdateType ut) {
		if (ut == UpdateType.SEARCH_RESULT){
			Media [] result = model.getSearchResult();
			
			System.out.println(result.length > 0?"":"No match found");
			System.out.println();
			for (Media mm : result){
				mm.DisplayItemDetails();
				System.out.println("Quantity: " + model.getItemQuantity(mm.getID()) + "\n");
			}
		}
		else if (ut == UpdateType.EDIT){
			Media [] result = model.getSearchResult();
			
			System.out.println(result.length > 0?"":"Item ID does not exist!");
			System.out.println();
			for (Media mm : result){
				mm.DisplayItemDetails();
				System.out.println("Quantity: " + model.getItemQuantity(mm.getID()) + "\n");
				
				System.out.println();
				System.out.println("Please enter the following information: \n");
				
				System.out.println("Title: ");
				String title = sc.nextLine().trim();
				System.out.println("Quantity: ");
				String quantity = sc.nextLine().trim();
				System.out.println("Genre: ");
				String genre = sc.nextLine().trim();
				System.out.println("Description: ");
				String description = sc.nextLine().trim();
				
				if (mm instanceof CD){				
					System.out.println("Artist(s): ");
					String artist = sc.nextLine().trim();
					System.out.println();
					
					if (title.equals(""))
						title = ((CD) mm).getTitle();
					if (genre.equals(""))
						genre = ((CD) mm).getGenre();
					if (description.equals(""))
						description = ((CD) mm).getDescription();
					if (artist.equals(""))
						artist = ((CD) mm).getArtist();
					
					controller.editItem(new CD(((CD) mm).getID(), title, description , genre, artist), quantity); 
				}
				else if (mm instanceof DVD){				
					System.out.println("Cast(s): ");
					String cast = sc.nextLine().trim();
					System.out.println();
					
					if (title.equals(""))
						title = ((DVD) mm).getTitle();
					if (genre.equals(""))
						genre = ((DVD) mm).getGenre();
					if (description.equals(""))
						description = ((DVD) mm).getDescription();
					if (cast.equals(""))
						cast = ((DVD) mm).getCast();
					
					controller.editItem(new DVD(((DVD) mm).getID(), title, description , genre, cast), quantity); 
				}
				else if (mm instanceof Book){				
					System.out.println("Author: ");
					String author = sc.nextLine().trim();
					System.out.println("ISBN: ");
					String ISBN = sc.nextLine().trim();
					System.out.println();
					
					if (title.equals(""))
						title = ((Book) mm).getTitle();
					if (genre.equals(""))
						genre = ((Book) mm).getGenre();
					if (description.equals(""))
						description = ((Book) mm).getDescription();
					if (author.equals(""))
						author = ((Book) mm).getAuthor();
					if (ISBN.equals(""))
						ISBN = ((Book) mm).getISBN();
					
					controller.editItem(new Book(((Book) mm).getID(), title, description , genre, author, ISBN), quantity); 
				}
			}
		}
		else if (ut == UpdateType.ID){
			itemID = model.getID();
		}

	}

}
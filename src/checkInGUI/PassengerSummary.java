package checkInGUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * 
 * Passenger Summary
 * Displays a list of passengers in the check in queue.
 * @author Amy McFarland
 *
 */
public class PassengerSummary extends JPanel{
	
	/**
	 * Default JAVA required for compatibility.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * List of passengers in the queue. Uses a default list model to
	 * store passenger information to display in the list component.
	 */
	private JList<String> passengerQueue = new JList<String>();
	private DefaultListModel<String> queueModel = new DefaultListModel<String>();
	
	/**
	 * Displays an information list of how many passengers are in the queue.
	 */
	private JLabel passengerDetails = new JLabel("There are currently 0 passengers waiting in the queue.");
	
	/**
	 * PassengerSummary
	 * Constructor, sets up the list of passengers in the queue.
	 */
	public PassengerSummary() {
		this.setLayout(new BorderLayout());
		passengerQueue = new JList<String>(queueModel);
		JScrollPane sp = new JScrollPane(passengerQueue);
		passengerQueue.setPreferredSize(new Dimension(200, 200));
		this.add(passengerDetails, BorderLayout.NORTH);
		this.add(sp, BorderLayout.SOUTH);
		this.setVisible(true);
	}
	
	/**
	 * addPassengerList
	 * Adds a passenger to the list of passengers in the queue,
	 * also updates the information label of how many passengers
	 * there are.
	 * @param bookingCode
	 * @param passengerName
	 */
	public void addPassengerList(String bookingCode, String passengerName) {
		int count = 0;
		boolean found = false;
		while (count < queueModel.getSize() && found == false) {
			if (queueModel.get(count).compareTo(bookingCode + " " + passengerName) == 0) {
				found = true;
			}
			count++;
		}
		if (found == false) {
			queueModel.addElement(bookingCode + " " + passengerName);
			passengerDetails
					.setText("There are currently " + queueModel.getSize() + " passengers waiting in the queue.");
		}
	}
}

=== Welcome to Simple Restaurant Billing System ===

Main Menu:
1. View Menu
2. Place Order
3. Admin - Add Menu Item
4. Admin - Remove Menu Item
5. Exit
Choose option: 2

----- MENU -----
 1. Margherita Pizza      Rs 199.00
 2. Veg Burger            Rs 99.00
 3. French Fries          Rs 79.00
 4. Caesar Salad          Rs 129.00
 5. Cold Coffee           Rs 89.00
 6. Paneer Butter Masala  Rs 179.00
Enter Menu ID to add to order (0 to finish): 1
Enter quantity: 2
Added: Margherita Pizza x2
Add more? (y/n): y
Enter Menu ID to add to order (0 to finish): 5
Enter quantity: 1
Added: Cold Coffee x1
Add more? (y/n): n

---------- RECEIPT ----------
Margherita Pizza      x 2  Rs 398.00
Cold Coffee           x 1  Rs 89.00
-----------------------------
Subtotal: Rs 487.00
GST (5.0%): Rs 24.35
Grand Total: Rs 511.35
-----------------------------
Receipt saved to receipt_1700000000000.txt
javac RestaurantBillingSystem.java
java RestaurantBillingSystem



import java.util.*;
import java.io.*;

class ConsoleColors {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
}

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ChatApp chatApp = new ChatApp();
        chatApp.run();
    }
}

class ChatApp {
    private AuthService authService = new AuthService();
    private ChatService chatService = new ChatService();
    private FriendService friendService = new FriendService(authService);
    private GroupChatService groupChatService;
    private User currentUser = null;

    private void viewGroupMessages(User user) {
        groupChatService.viewGroupMessages(user);
    }

    public ChatApp() {
        this.groupChatService = new GroupChatService(authService);
    }

    public void run() {
        while (true) {
            System.out.println(ConsoleColors.CYAN + "\n=== Welcome to ConsoleChat ===" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "1. Sign Up" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "2. Log In" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "3. Exit" + ConsoleColors.RESET);
            System.out.print(ConsoleColors.PURPLE + "Choose an option: " + ConsoleColors.RESET);

            String choice = new Scanner(System.in).nextLine();

            switch (choice) {
                case "1":
                    authService.signUp();
                    break;
                case "2":
                    currentUser = authService.logIn();
                    if (currentUser != null) {
                        mainMenu();
                        currentUser.setOnline(false); // Set offline on logout
                    }
                    break;
                case "3":
                    System.out.println(ConsoleColors.GREEN + "Goodbye!" + ConsoleColors.RESET);
                    return;
                default:
                    System.out.println(ConsoleColors.RED + "Invalid option. Try again." + ConsoleColors.RESET);
            }
        }
    }

    private void mainMenu() {
        while (true) {
            System.out.println(ConsoleColors.CYAN + "\n=== Main Menu ===" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "1. View Inbox" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "2. Send Message" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "3. Manage Friends" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "4. Send Group Message" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "5. View Group Messages" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "6. Block/Unblock Users" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "7. Delete Message from Inbox" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "8. Search Inbox Messages" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "9. Sort Inbox Messages" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "10. Search Group Messages" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "11. Sort Group Messages" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "12. Logout" + ConsoleColors.RESET);

            System.out.print(ConsoleColors.PURPLE + "Choose an option: " + ConsoleColors.RESET);

            String option = new Scanner(System.in).nextLine();

            switch (option) {
                case "1":
                    chatService.viewInbox(currentUser);
                    break;
                case "2":
                    chatService.sendMessage(currentUser, authService);
                    break;
                case "3":
                    friendService.manageFriends(currentUser);
                    break;
                case "4":
                    groupChatService.manageGroups(currentUser);
                    break;
                case "5":
                    viewGroupMessages(currentUser);
                    break;
                case "6":
                    manageBlockList(currentUser);
                    break;
                case "7":
                    chatService.deleteMessage(currentUser);
                    break;
                case "8":
                    chatService.searchMessages(currentUser);
                    break;
                case "9":
                    System.out.print(ConsoleColors.PURPLE + "Sort by (1) Oldest or (2) Newest: " + ConsoleColors.RESET);
                    String order = new Scanner(System.in).nextLine();
                    chatService.sortMessages(currentUser, order.equals("1"));
                    break;
                case "10":
                    groupChatService.searchGroupMessages(currentUser);
                    break;
                case "11":
                    groupChatService.sortGroupMessages(currentUser);
                    break;
                case "12":
                    return;
                default:
                    System.out.println(ConsoleColors.RED + "Invalid option. Try again." + ConsoleColors.RESET);
            }
        }
    }

    // Block/Unblock Users
    private void manageBlockList(User user) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(ConsoleColors.CYAN + "\n=== Block/Unblock Users ===" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "1. Block a user" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "2. Unblock a user" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "3. View blocked users" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "4. Back" + ConsoleColors.RESET);
            System.out.print(ConsoleColors.PURPLE + "Choose an option: " + ConsoleColors.RESET);
            String choice = sc.nextLine();
            if (choice.equals("1")) {
                System.out.print(ConsoleColors.PURPLE + "Enter username to block: " + ConsoleColors.RESET);
                String toBlock = sc.nextLine();
                if (toBlock.equals(user.getUsername())) {
                    System.out.println(ConsoleColors.RED + "You cannot block yourself." + ConsoleColors.RESET);
                } else if (user.getBlockedUsers().contains(toBlock)) {
                    System.out.println(ConsoleColors.YELLOW + "User already blocked." + ConsoleColors.RESET);
                } else {
                    user.getBlockedUsers().add(toBlock);
                    System.out.println(ConsoleColors.GREEN + "Blocked " + toBlock + ConsoleColors.RESET);
                }
            } else if (choice.equals("2")) {
                System.out.print(ConsoleColors.PURPLE + "Enter username to unblock: " + ConsoleColors.RESET);
                String toUnblock = sc.nextLine();
                if (user.getBlockedUsers().remove(toUnblock)) {
                    System.out.println(ConsoleColors.GREEN + "Unblocked " + toUnblock + ConsoleColors.RESET);
                } else {
                    System.out.println(ConsoleColors.YELLOW + "User was not blocked." + ConsoleColors.RESET);
                }
            } else if (choice.equals("3")) {
                System.out.println(ConsoleColors.CYAN + "Blocked users: " + user.getBlockedUsers() + ConsoleColors.RESET);
            } else if (choice.equals("4")) {
                return;
            } else {
                System.out.println(ConsoleColors.RED + "Invalid option." + ConsoleColors.RESET);
            }
        }
    }
}

class User {
    private String username;
    private String password;
    private List<String> friends = new ArrayList<>();
    private List<String> sentRequests = new ArrayList<>();
    private List<String> receivedRequests = new ArrayList<>();
    private List<String> groups = new ArrayList<>();
    private Set<String> blockedUsers = new HashSet<>();
    private boolean online = false; // Automatic status
    System.out.println("hellooooooo");

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public List<String> getFriends() { return friends; }
    public List<String> getSentRequests() { return sentRequests; }
    public List<String> getReceivedRequests() { return receivedRequests; }
    public List<String> getGroups() { return groups; }
    public Set<String> getBlockedUsers() { return blockedUsers; }
    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }
}

class AuthService {
    private Map<String, User> users = new HashMap<>();

    public void signUp() {
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.PURPLE + "Enter username: " + ConsoleColors.RESET);
        String username = sc.nextLine();
        if (users.containsKey(username)) {
            System.out.println(ConsoleColors.RED + "Username already exists." + ConsoleColors.RESET);
            return;
        }
        System.out.print(ConsoleColors.PURPLE + "Enter password: " + ConsoleColors.RESET);
        String password = sc.nextLine();
        users.put(username, new User(username, password));
        System.out.println(ConsoleColors.GREEN + "Sign up successful." + ConsoleColors.RESET);
    }

    public User logIn() {
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.PURPLE + "Enter username: " + ConsoleColors.RESET);
        String username = sc.nextLine();
        System.out.print(ConsoleColors.PURPLE + "Enter password: " + ConsoleColors.RESET);
        String password = sc.nextLine();
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            user.setOnline(true); // Set online
            System.out.println(ConsoleColors.GREEN + "Login successful. Welcome, " + username + "!" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "Your status: " + (user.isOnline() ? "Active" : "Offline") + ConsoleColors.RESET);
            return user;
        } else {
            System.out.println(ConsoleColors.RED + "Invalid credentials." + ConsoleColors.RESET);
            return null;
        }
    }

    public Map<String, User> getUsers() {
        return users;
    }
}

class ChatService {
    private Map<String, Queue<Message>> messageQueue = new HashMap<>();

    // Block feature: Check if recipient has blocked sender
    public void sendMessage(User sender, AuthService authService) {
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.PURPLE + "Enter recipient username: " + ConsoleColors.RESET);
        String recipient = sc.nextLine();
        if (recipient.equals(sender.getUsername())) {
            System.out.println(ConsoleColors.RED + "You cannot send a message to yourself." + ConsoleColors.RESET);
            return;
        }
        User recipientUser = authService.getUsers().get(recipient);
        if (recipientUser == null) {
            System.out.println(ConsoleColors.RED + "User does not exist." + ConsoleColors.RESET);
            return;
        }
        if (recipientUser.getBlockedUsers().contains(sender.getUsername())) {
            System.out.println(ConsoleColors.RED + "You are blocked by this user. Message not sent." + ConsoleColors.RESET);
            return;
        }
        System.out.print(ConsoleColors.PURPLE + "Enter message: " + ConsoleColors.RESET);
        String content = sc.nextLine();
        Message message = new Message(sender.getUsername(), content, new Date());

        messageQueue.computeIfAbsent(recipient, k -> new LinkedList<>()).add(message);
        System.out.println(ConsoleColors.GREEN + "Message sent to " + recipient + " (Status: " + (recipientUser.isOnline() ? "Active" : "Offline") + ")" + ConsoleColors.RESET);
    }

    // Delete message from inbox (by index)
    public void deleteMessage(User user) {
        Queue<Message> inbox = messageQueue.getOrDefault(user.getUsername(), new LinkedList<>());
        if (inbox.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "Inbox is empty." + ConsoleColors.RESET);
            return;
        }
        List<Message> messages = new ArrayList<>(inbox);
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            System.out.println(ConsoleColors.YELLOW + (i+1) + ". From: " + msg.getSender() + " | Time: " + msg.getTimestamp() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN + "   Message: " + msg.getContent() + ConsoleColors.RESET);
        }
        System.out.print(ConsoleColors.PURPLE + "Enter message number to delete (or 0 to cancel): " + ConsoleColors.RESET);
        Scanner sc = new Scanner(System.in);
        int idx;
        try {
            idx = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Invalid input." + ConsoleColors.RESET);
            return;
        }
        if (idx < 1 || idx > messages.size()) {
            System.out.println(ConsoleColors.YELLOW + "Cancelled or invalid choice." + ConsoleColors.RESET);
            return;
        }
        Message toRemove = messages.get(idx-1);
        inbox.remove(toRemove);
        System.out.println(ConsoleColors.GREEN + "Message deleted." + ConsoleColors.RESET);
    }

    // Search messages in personal inbox
    public void searchMessages(User user) {
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.PURPLE + "Enter keyword to search: " + ConsoleColors.RESET);
        String keyword = sc.nextLine().toLowerCase();

        Queue<Message> inbox = messageQueue.getOrDefault(user.getUsername(), new LinkedList<>());
        boolean found = false;

        for (Message msg : inbox) {
            if (msg.getContent().toLowerCase().contains(keyword)) {
                System.out.println(ConsoleColors.CYAN + "From: " + msg.getSender() + " | Time: " + msg.getTimestamp() + ConsoleColors.RESET);
                System.out.println(ConsoleColors.YELLOW + "Message: " + msg.getContent() + ConsoleColors.RESET);
                System.out.println(ConsoleColors.BLUE + "-------------------------" + ConsoleColors.RESET);
                found = true;
            }
        }

        if (!found) {
            System.out.println(ConsoleColors.RED + "No matching messages found." + ConsoleColors.RESET);
        }
    }

    // Sort messages by timestamp (asc/desc)
    public void sortMessages(User user, boolean ascending) {
        Queue<Message> inbox = messageQueue.getOrDefault(user.getUsername(), new LinkedList<>());
        List<Message> sorted = new ArrayList<>(inbox);

        sorted.sort((m1, m2) -> ascending ? m1.getTimestamp().compareTo(m2.getTimestamp())
                : m2.getTimestamp().compareTo(m1.getTimestamp()));

        System.out.println(ConsoleColors.CYAN + "\n=== Sorted Inbox Messages ===" + ConsoleColors.RESET);
        for (Message msg : sorted) {
            System.out.println(ConsoleColors.CYAN + "From: " + msg.getSender() + " | Time: " + msg.getTimestamp() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "Message: " + msg.getContent() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "-------------------------" + ConsoleColors.RESET);
        }
    }

    public void viewInbox(User user) {
        Queue<Message> inbox = messageQueue.getOrDefault(user.getUsername(), new LinkedList<>());
        if (inbox.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "Inbox is empty." + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.CYAN + "\n=== Inbox ===" + ConsoleColors.RESET);
            for (Message msg : inbox) {
                System.out.println(ConsoleColors.CYAN + "From: " + msg.getSender() + " | Time: " + msg.getTimestamp() + ConsoleColors.RESET);
                System.out.println(ConsoleColors.YELLOW + "Message: " + msg.getContent() + ConsoleColors.RESET);
                System.out.println(ConsoleColors.BLUE + "-------------------------" + ConsoleColors.RESET);
            }
        }
    }
}

class FriendService {
    private AuthService authService;

    public FriendService(AuthService authService) {
        this.authService = authService;
    }

    public void manageFriends(User currentUser) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(ConsoleColors.CYAN + "\n=== Friend Management ===" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "1. Send Friend Request" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "2. View Friend Requests" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "3. View Friend List" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "4. Back to Main Menu" + ConsoleColors.RESET);
            System.out.print(ConsoleColors.PURPLE + "Choose an option: " + ConsoleColors.RESET);
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    System.out.print(ConsoleColors.PURPLE + "Enter username to send request: " + ConsoleColors.RESET);
                    String target = sc.nextLine();
                    Map<String, User> users = authService.getUsers();
                    if (!users.containsKey(target)) {
                        System.out.println(ConsoleColors.RED + "User not found." + ConsoleColors.RESET);
                    } else if (target.equals(currentUser.getUsername())) {
                        System.out.println(ConsoleColors.RED + "You can't add yourself." + ConsoleColors.RESET);
                    } else {
                        User targetUser = users.get(target);
                        if (currentUser.getFriends().contains(target)) {
                            System.out.println(ConsoleColors.YELLOW + "Already friends." + ConsoleColors.RESET);
                        } else {
                            currentUser.getSentRequests().add(target);
                            targetUser.getReceivedRequests().add(currentUser.getUsername());
                            System.out.println(ConsoleColors.GREEN + "Friend request sent to " + target + ConsoleColors.RESET);
                        }
                    }
                    break;
                case "2":
                    List<String> requests = currentUser.getReceivedRequests();
                    if (requests.isEmpty()) {
                        System.out.println(ConsoleColors.YELLOW + "No friend requests." + ConsoleColors.RESET);
                    } else {
                        for (String sender : requests) {
                            System.out.println(ConsoleColors.CYAN + "Friend request from: " + sender + ConsoleColors.RESET);
                            System.out.print(ConsoleColors.PURPLE + "Accept (a) / Reject (r): " + ConsoleColors.RESET);
                            String decision = sc.nextLine();
                            if (decision.equalsIgnoreCase("a")) {
                                currentUser.getFriends().add(sender);
                                authService.getUsers().get(sender).getFriends().add(currentUser.getUsername());
                                authService.getUsers().get(sender).getSentRequests().remove(currentUser.getUsername());
                                System.out.println(ConsoleColors.GREEN + "Friend request accepted." + ConsoleColors.RESET);
                            } else {
                                authService.getUsers().get(sender).getSentRequests().remove(currentUser.getUsername());
                                System.out.println(ConsoleColors.YELLOW + "Friend request rejected." + ConsoleColors.RESET);
                            }
                        }
                        requests.clear();
                    }
                    break;
                case "3":
                    if (currentUser.getFriends().isEmpty()) {
                        System.out.println(ConsoleColors.YELLOW + "No friends yet." + ConsoleColors.RESET);
                    } else {
                        System.out.println(ConsoleColors.CYAN + "Your friends:" + ConsoleColors.RESET);
                        for (String friend : currentUser.getFriends()) {
                            User friendUser = authService.getUsers().get(friend);
                            String status = (friendUser != null && friendUser.isOnline()) ? "Active" : "Offline";
                            System.out.println(ConsoleColors.GREEN + "- " + friend + " (" + status + ")" + ConsoleColors.RESET);
                        }
                    }
                    break;
                case "4":
                    return;
                default:
                    System.out.println(ConsoleColors.RED + "Invalid option." + ConsoleColors.RESET);
            }
        }
    }
}

class GroupChatService {
    private Map<String, GroupChat> groups = new HashMap<>();
    private AuthService authService;

    public GroupChatService(AuthService authService) {
        this.authService = authService;
    }

    public void searchGroupMessages(User user) {
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.PURPLE + "Enter group name: " + ConsoleColors.RESET);
        String groupName = sc.nextLine();

        GroupChat group = groups.get(groupName);
        if (group == null || !group.getMembers().contains(user.getUsername())) {
            System.out.println(ConsoleColors.RED + "Group not found or access denied." + ConsoleColors.RESET);
            return;
        }

        System.out.print(ConsoleColors.PURPLE + "Enter keyword to search: " + ConsoleColors.RESET);
        String keyword = sc.nextLine().toLowerCase();
        boolean found = false;

        for (Message msg : group.getMessages()) {
            if (msg.getContent().toLowerCase().contains(keyword)) {
                System.out.println(ConsoleColors.CYAN + "From: " + msg.getSender() + " | Time: " + msg.getTimestamp() + ConsoleColors.RESET);
                System.out.println(ConsoleColors.YELLOW + "Message: " + msg.getContent() + ConsoleColors.RESET);
                System.out.println(ConsoleColors.BLUE + "-------------------------" + ConsoleColors.RESET);
                found = true;
            }
        }

        if (!found) {
            System.out.println(ConsoleColors.RED + "No matching messages found." + ConsoleColors.RESET);
        }
    }

    public void sortGroupMessages(User user) {
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.PURPLE + "Enter group name: " + ConsoleColors.RESET);
        String groupName = sc.nextLine();

        GroupChat group = groups.get(groupName);
        if (group == null || !group.getMembers().contains(user.getUsername())) {
            System.out.println(ConsoleColors.RED + "Group not found or access denied." + ConsoleColors.RESET);
            return;
        }
        System.out.print(ConsoleColors.PURPLE + "Sort by (1) Oldest or (2) Newest: " + ConsoleColors.RESET);
        String order = sc.nextLine();
        List<Message> sorted = new ArrayList<>(group.getMessages());

        sorted.sort((m1, m2) -> order.equals("1") ? m1.getTimestamp().compareTo(m2.getTimestamp())
                : m2.getTimestamp().compareTo(m1.getTimestamp()));

        for (Message msg : sorted) {
            System.out.println(ConsoleColors.CYAN + "From: " + msg.getSender() + " | Time: " + msg.getTimestamp() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "Message: " + msg.getContent() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "-------------------------" + ConsoleColors.RESET);
        }
    }

    public void createGroup(User creator) {
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.PURPLE + "Enter group name: " + ConsoleColors.RESET);
        String groupName = sc.nextLine();
        GroupChat group = new GroupChat(groupName, creator);
        groups.put(groupName, group);
        creator.getGroups().add(groupName);
        System.out.println(ConsoleColors.GREEN + "Group created successfully." + ConsoleColors.RESET);
    }

    public void addUserToGroup(User creator) {
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.PURPLE + "Enter group name to add user: " + ConsoleColors.RESET);
        String groupName = sc.nextLine();
        System.out.print(ConsoleColors.PURPLE + "Enter username to add to group: " + ConsoleColors.RESET);
        String username = sc.nextLine();

        GroupChat group = groups.get(groupName);
        if (group != null && group.getCreator().equals(creator)) {
            User targetUser = authService.getUsers().get(username);
            if (targetUser == null) {
                System.out.println(ConsoleColors.RED + "User does not exist." + ConsoleColors.RESET);
                return;
            }
            group.getMembers().add(username);
            targetUser.getGroups().add(groupName);
            System.out.println(ConsoleColors.GREEN + username + " added to the group." + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.RED + "You are not the creator of the group or the group doesn't exist." + ConsoleColors.RESET);
        }
    }

    public void sendGroupMessage(User sender) {
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.PURPLE + "Enter group name: " + ConsoleColors.RESET);
        String groupName = sc.nextLine();

        GroupChat group = groups.get(groupName);
        if (group == null || !group.getMembers().contains(sender.getUsername())) {
            System.out.println(ConsoleColors.RED + "Group not found or access denied." + ConsoleColors.RESET);
            return;
        }

        System.out.print(ConsoleColors.PURPLE + "Enter message: " + ConsoleColors.RESET);
        String content = sc.nextLine();
        Message message = new Message(sender.getUsername(), content, new Date());
        group.getMessages().add(message);
        System.out.println(ConsoleColors.GREEN + "Message sent to group " + groupName + ConsoleColors.RESET);
    }

    public void viewGroupMessages(User user) {
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.PURPLE + "Enter group name: " + ConsoleColors.RESET);
        String groupName = sc.nextLine();

        GroupChat group = groups.get(groupName);
        if (group == null || !group.getMembers().contains(user.getUsername())) {
            System.out.println(ConsoleColors.RED + "Group not found or access denied." + ConsoleColors.RESET);
            return;
        }

        if (group.getMessages().isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "No messages in this group." + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.CYAN + "\n=== Group Messages ===" + ConsoleColors.RESET);
            for (Message msg : group.getMessages()) {
                System.out.println(ConsoleColors.CYAN + "From: " + msg.getSender() + " | Time: " + msg.getTimestamp() + ConsoleColors.RESET);
                System.out.println(ConsoleColors.YELLOW + "Message: " + msg.getContent() + ConsoleColors.RESET);
                System.out.println(ConsoleColors.BLUE + "-------------------------" + ConsoleColors.RESET);
            }
        }
    }

    public void manageGroups(User user) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(ConsoleColors.CYAN + "\n=== Group Chat Management ===" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "1. Create Group" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "2. Add User to Group" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "3. Send Group Message" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW + "4. Back to Main Menu" + ConsoleColors.RESET);
            System.out.print(ConsoleColors.PURPLE + "Choose an option: " + ConsoleColors.RESET);

            String option = sc.nextLine();

            switch (option) {
                case "1":
                    createGroup(user);
                    break;
                case "2":
                    addUserToGroup(user);
                    break;
                case "3":
                    sendGroupMessage(user);
                    break;
                case "4":
                    return;
                default:
                    System.out.println(ConsoleColors.RED + "Invalid option." + ConsoleColors.RESET);
            }
        }
    }
}

class GroupChat {
    private String name;
    private User creator;
    private Set<String> members = new HashSet<>();
    private List<Message> messages = new ArrayList<>();

    public GroupChat(String name, User creator) {
        this.name = name;
        this.creator = creator;
        this.members.add(creator.getUsername());
    }
    public String getName() { return name; }
    public User getCreator() { return creator; }
    public Set<String> getMembers() { return members; }
    public List<Message> getMessages() { return messages; }
}

class Message {
    private String sender;
    private String content;
    private Date timestamp;

    public Message(String sender, String content, Date timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public Date getTimestamp() { return timestamp; }
}
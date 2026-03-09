<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Chat" %>
<%@ page import="model.Message" %>

<%!
    public String escapeHtml(String text) {
        if (text == null) return "";
        text = text.replace("&", "&amp;");
        text = text.replace("<", "&lt;");
        text = text.replace(">", "&gt;");
        text = text.replace("\"", "&quot;");
        return text;
    }

    public String formatMessage(String text) {
        if (text == null) return "";

        String escaped = escapeHtml(text);

        StringBuilder result = new StringBuilder();
        boolean inCodeBlock = false;

        String[] lines = escaped.split("\\r?\\n");

        for (String line : lines) {
            String trimmed = line.trim();

            if (trimmed.startsWith("```")) {
                if (!inCodeBlock) {
                    result.append("<pre class='code-block'><code>");
                    inCodeBlock = true;
                } else {
                    result.append("</code></pre>");
                    inCodeBlock = false;
                }
                continue;
            }

            if (inCodeBlock) {
                result.append(line).append("\n");
            } else {
                String inlineFormatted = line.replaceAll("`([^`]+)`", "<span class='inline-code'>$1</span>");
                result.append(inlineFormatted).append("<br>");
            }
        }

        if (inCodeBlock) {
            result.append("</code></pre>");
        }

        return result.toString();
    }
%>

<%
Integer userId = (Integer) session.getAttribute("userId");
String userEmail = (String) session.getAttribute("userEmail");

if (userId == null) {
    response.sendRedirect("login.jsp");
    return;
}

String initial = "U";
if (userEmail != null && !userEmail.trim().isEmpty()) {
    initial = userEmail.substring(0, 1).toUpperCase();
}

List<Chat> chatList = (List<Chat>) request.getAttribute("chatList");
List<Message> messages = (List<Message>) request.getAttribute("messages");
Chat selectedChat = (Chat) request.getAttribute("selectedChat");
Integer remainingMessages = (Integer) request.getAttribute("remainingMessages");
Integer dailyLimit = (Integer) request.getAttribute("dailyLimit");
String errorMessage = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ChatOrbit Dashboard</title>
    <link rel="stylesheet" href="assets/css/dashboard.css">
</head>
<body>

<div class="app-shell">

    <aside class="sidebar">
        <div class="brand-section">
            <div class="brand-logo">CO</div>
            <div class="brand-text">
                <h2>ChatOrbit</h2>
                <p>Your AI Workspace</p>
            </div>
        </div>

        <a href="new-chat" class="new-chat-btn">+ New Chat</a>

        <div class="usage-box">
            Daily Usage: <strong><%= remainingMessages %></strong> / <strong><%= dailyLimit %></strong> left
        </div>

        <div class="sidebar-section">
            <p class="sidebar-heading">History</p>

            <div class="history-list">
                <%
                if (chatList != null && !chatList.isEmpty()) {
                    for (Chat chat : chatList) {
                        boolean active = selectedChat != null && selectedChat.getId() == chat.getId();
                %>
                    <a href="load-chat?chatId=<%= chat.getId() %>"
                       class="history-item <%= active ? "active" : "" %>">
                        <span class="history-title"><%= chat.getTitle() %></span>
                    </a>
                <%
                    }
                } else {
                %>
                    <div class="history-empty">No chats yet</div>
                <%
                }
                %>
            </div>
        </div>
    </aside>

    <main class="main-panel">

        <header class="topbar">
            <div class="topbar-title">
                <%= selectedChat != null ? selectedChat.getTitle() : "New Chat" %>
            </div>

            <div class="user-menu">
                <button class="user-avatar" id="userAvatarBtn" type="button"><%= initial %></button>

                <div class="user-dropdown" id="userDropdown">
                    <div class="user-email"><%= userEmail %></div>
                    <a href="logout" class="logout-link">Logout</a>
                </div>
            </div>
        </header>

        <section class="chat-area" id="chatArea">

            <%
            if (errorMessage != null && !errorMessage.trim().isEmpty()) {
            %>
                <div class="alert-box">
                    <%= errorMessage %>
                </div>
            <%
            }
            %>

            <%
            boolean hasMessages = messages != null && !messages.isEmpty();
            if (!hasMessages) {
            %>
            <div class="welcome-section" id="welcomeSection">
                <div class="welcome-icon">◎</div>
                <h1>How can ChatOrbit help you today?</h1>
                <p>Ask anything about Java, JSP, Servlets, JDBC, MySQL, or your college project work.</p>

                <div class="prompt-grid">
                    <div class="prompt-card" onclick="fillPrompt('Explain the difference between Thread class and Runnable interface in Java')">
                        Explain Java threading basics
                    </div>

                    <div class="prompt-card" onclick="fillPrompt('Teach me request dispatcher in servlets with examples')">
                        Teach request dispatcher
                    </div>

                    <div class="prompt-card" onclick="fillPrompt('How do sessions work in JSP and Servlets?')">
                        Explain session handling
                    </div>

                    <div class="prompt-card" onclick="fillPrompt('Give me a JDBC CRUD project idea with authentication')">
                        Suggest a JDBC project
                    </div>
                </div>
            </div>
            <%
            }
            %>

            <div class="messages-container" id="messagesContainer" style="<%= hasMessages ? "display:flex;" : "display:none;" %>">
                <%
                if (messages != null) {
                    for (Message message : messages) {
                        boolean isUser = "USER".equalsIgnoreCase(message.getSender());
                %>
                    <div class="message-row <%= isUser ? "user-row" : "ai-row" %>">
                        <div class="message-bubble <%= isUser ? "user-bubble" : "ai-bubble" %>">
                            <%= formatMessage(message.getMessageText()) %>
                        </div>
                    </div>
                <%
                    }
                }
                %>

                <div class="message-row ai-row" id="loaderRow" style="display:none;">
                    <div class="message-bubble ai-bubble loader-bubble">
                        <span></span>
                        <span></span>
                        <span></span>
                    </div>
                </div>
            </div>
        </section>

        <footer class="input-section">
            <form class="prompt-form" action="chat" method="post" onsubmit="return showLoader()">
                <input type="hidden" name="chatId" value="<%= selectedChat != null ? selectedChat.getId() : "" %>">

                <input
                    type="text"
                    id="promptInput"
                    name="prompt"
                    class="prompt-input"
                    placeholder="Message ChatOrbit..."
                    autocomplete="off"
                    required
                >

                <button type="submit" class="send-btn">Send</button>
            </form>
        </footer>

    </main>
</div>
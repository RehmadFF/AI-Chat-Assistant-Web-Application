const userAvatarBtn = document.getElementById("userAvatarBtn");
const userDropdown = document.getElementById("userDropdown");
const promptInput = document.getElementById("promptInput");
const welcomeSection = document.getElementById("welcomeSection");
const messagesContainer = document.getElementById("messagesContainer");
const loaderRow = document.getElementById("loaderRow");
const chatArea = document.getElementById("chatArea");

if (userAvatarBtn) {
    userAvatarBtn.addEventListener("click", function (e) {
        e.stopPropagation();
        userDropdown.classList.toggle("show");
    });
}

document.addEventListener("click", function (e) {
    if (userDropdown && !userDropdown.contains(e.target) && e.target !== userAvatarBtn) {
        userDropdown.classList.remove("show");
    }
});

function fillPrompt(text) {
    promptInput.value = text;
    promptInput.focus();
}

function showLoader() {
    const text = promptInput.value.trim();

    if (text === "") {
        return false;
    }

    if (welcomeSection) {
        welcomeSection.style.display = "none";
    }

    if (messagesContainer) {
        messagesContainer.style.display = "flex";
    }

    if (loaderRow) {
        loaderRow.style.display = "flex";
    }

    setTimeout(scrollToBottom, 50);
    return true;
}

function scrollToBottom() {
    if (chatArea) {
        chatArea.scrollTop = chatArea.scrollHeight;
    }
}

window.addEventListener("load", function () {
    scrollToBottom();
});

window.fillPrompt = fillPrompt;
window.showLoader = showLoader;

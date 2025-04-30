document.addEventListener("DOMContentLoaded", () => {
    fetchLiveNews();
});

const API_KEY = "9e3acfb1d9934745a7ae4d64c6e9b2ba";
const NEWS_API_URL = `https://newsapi.org/v2/top-headlines?country=us&apiKey=${API_KEY}`;

async function fetchLiveNews() {
    const newsContainer = document.getElementById("news-container");
    newsContainer.innerHTML = "Loading news...";

    try {
        const response = await fetch(NEWS_API_URL);
        const data = await response.json();

        if (!data.articles || data.articles.length === 0) {
            throw new Error("No articles available.");
        }

        newsContainer.innerHTML = "";
        data.articles.forEach(article => {
            const articleElement = document.createElement("div");
            articleElement.className = "news-item";
            articleElement.innerHTML = `
                <h3>${article.title}</h3>
                <p>${article.description || "No description available."}</p>
                <a href="${article.url}" target="_blank">Read more</a>
                <button onclick="checkFakeNews('${article.title}',true)">Verify</button>
            `;
            newsContainer.appendChild(articleElement);
        });

    } catch (error) {
        newsContainer.innerHTML = `Error loading news: ${error.message}`;
    }
}
function verifyCustomNews() {
    const customNewsText = document.getElementById("custom-news").value.trim();

    if (!customNewsText) {
        alert("Please enter news text to verify.");
        return;
    }

    checkFakeNews(customNewsText,false);
}


async function checkFakeNews(newsText, isLiveNews) {
    const resultElement = document.getElementById("result");
    resultElement.innerHTML = "Verifying...";

    try {
        const response = await fetch("http://localhost:8099/api/verify-news", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ text: newsText, isLiveNews })
        });

        const data = await response.json();
        resultElement.innerHTML = `Result: ${data.result}`;
    } catch (error) {
        resultElement.innerHTML = "Error verifying news.";
    }
}


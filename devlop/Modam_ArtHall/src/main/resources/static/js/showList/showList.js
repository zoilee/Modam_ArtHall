        // Top 버튼 동작
        window.onscroll = function () {
            const topButton = document.getElementById("topButton");
            if (document.body.scrollTop > 200 || document.documentElement.scrollTop > 200) {
                topButton.style.display = "block";
            } else {
                topButton.style.display = "none";
            }
        };
    
        // Top 버튼 이동 함수
        function scrollToTop() {
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }

        document.addEventListener("DOMContentLoaded", function () {
            const buttons = document.querySelectorAll(".page-link");
            buttons.forEach(button => {
                button.addEventListener("click", function () {
                    const page = this.getAttribute("data-page");
                    console.log('Navigating to page:', page);
                    loadPage(page);
                });
            });
        });

        // loadPage 함수를 전역(global)으로 등록
        window.loadPage = function(page) {
            const url = `/showListFragment?page=${page}&size=5`;

            fetch(url)
                .then(response => {
                    console.log("Response Status:", response.status);
                    if (!response.ok) {
                        return response.text().then(text => {
                            console.error("Error Response Text:", text);
                            throw new Error(`HTTP error! status: ${response.status}`);
                        });
                    }
                    return response.text();
                })
                .then(html => {
                    const container = document.querySelector("#pastPerformances tbody");
                    if (container) {
                        container.innerHTML = html;
                    } else {
                        console.error("#pastPerformances tbody not found.");
                    }
                })
                .catch(error => {
                    console.error("Error loading page:", error);
                    alert("페이지를 로드하는 중 오류가 발생했습니다.");
                });
        };
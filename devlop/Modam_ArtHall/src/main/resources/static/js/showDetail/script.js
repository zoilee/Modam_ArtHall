window.onscroll = function() { toggleScrollToTopBtn() };

function toggleScrollToTopBtn() {
    const scrollToTopBtn = document.getElementById("sd_scrollToTopBtn");
    const footer = document.getElementById("footer");
    const footerRect = footer.getBoundingClientRect();
    const windowHeight = window.innerHeight;
    
    // 스크롤 위치가 100px 이상일 때 버튼 표시
    if (window.pageYOffset > 100) {
        scrollToTopBtn.style.display = "block";
    } else {
        scrollToTopBtn.style.display = "none";
    }

    // 푸터에 닿으면 버튼 위치 조정
    if (footerRect.top < windowHeight) {
        const overlap = windowHeight - footerRect.top + 20;
        scrollToTopBtn.style.transform = `translateY(-${overlap}px)`;
    } else {
        scrollToTopBtn.style.transform = "translateY(0)";
    }
}

// 맨 위로 스크롤
function scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
}
$(document).ready(function () {
    //폼 지정 및 제출
    const form = document.querySelector("#formToReservForm");
    form.submit();
});            


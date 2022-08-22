function load() {
	const listFooter = document.querySelector(".list-footer");
	
	if(getUser() == null) {
		if(getUser().userRoles.includes("ROLE_ADMIN")) {
			listFooter.innerHTML += `
				<button type="button" class="notice-insert-button">글 쓰기</button>
			`;
			
			const noticeAddButton = document.querySelector(".notice-insert-button");
			
			noticeAddButton.onclick = () => {
				location.href = "/notice/addition";
			}
		}
	}
}

load();
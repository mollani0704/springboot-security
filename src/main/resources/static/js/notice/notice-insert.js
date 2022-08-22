
let oEditors = [];
 	nhn.husky.EZCreator.createInIFrame({
 	oAppRef: oEditors,
         elPlaceHolder: "ir1",
         sSkinURI: "/static/SmartEditor2Skin.html",
         fCreator: "createSEditor2"
        });

        
        const submitButton = document.querySelector(".submit");

 submitButton.onclick = () => {
        /*
                editor의 내용을 textarea롤 옮겨주는 역할
            */
      oEditors.getById["ir1"].exec("UPDATE_CONTENTS_FIELD", []);
      const textareaValue = document.querySelector("#ir1").value;
      
      let formData = new FormData(document.querySelector("form"));
      
      formData.forEach((value, key) => {
		console.log("key : " + key);
		console.log("value : " + value)
	});
	
	$.ajax({
		async : false,
		type : "post", 
		url : "/api/v1/notice",
		enctype : "multipart/form-data",
		contentType : false,
		processData : false,
		data : formData,
		dataType : "json",
		success : (response) => {
			
		}, 
		error : (error) => {
			console.log(error);
		}
		
	})
}
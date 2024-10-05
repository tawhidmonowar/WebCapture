const modal = document.getElementById("imageModal");
const img = document.getElementById("clickableImage");
const modalImg = document.getElementById("modalImage");
const closeButton = document.getElementsByClassName("close")[0];

// When the user clicks on the image, open the modal
img.onclick = function() {
  modal.style.display = "flex";
  modalImg.src = this.src.replace('small', 'large');
}

// When the user clicks on <span> (x), close the modal
closeButton.onclick = function() {
  modal.style.display = "none";
}

// Close the modal when the user clicks anywhere outside of the modal image
window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}

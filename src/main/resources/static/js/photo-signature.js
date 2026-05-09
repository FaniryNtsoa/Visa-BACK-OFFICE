(function () {
  const video = document.getElementById("photoVideo");
  const canvas = document.getElementById("photoCanvas");
  const startBtn = document.getElementById("startCameraBtn");
  const captureBtn = document.getElementById("capturePhotoBtn");
  const retakeBtn = document.getElementById("retakePhotoBtn");
  const photoInput = document.getElementById("photoData");
  const signatureCanvas = document.getElementById("signatureCanvas");
  const signatureInput = document.getElementById("signatureData");
  const clearSignatureBtn = document.getElementById("clearSignatureBtn");
  const form = document.querySelector("form");

  if (!video || !canvas || !signatureCanvas || !form) {
    return;
  }

  const photoCtx = canvas.getContext("2d");
  const signatureCtx = signatureCanvas.getContext("2d");
  let stream = null;
  let isDrawing = false;
  let hasSignature = false;

  signatureCtx.lineWidth = 2.2;
  signatureCtx.lineCap = "round";
  signatureCtx.strokeStyle = "#111827";

  function showLiveVideo() {
    video.classList.remove("is-hidden");
    canvas.classList.add("is-hidden");
  }

  function showCapturedPhoto() {
    canvas.classList.remove("is-hidden");
    video.classList.add("is-hidden");
  }

  async function startCamera() {
    if (stream) {
      return;
    }
    try {
      stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: "user" },
        audio: false
      });
      video.srcObject = stream;
      showLiveVideo();
    } catch (error) {
      alert("Impossible d'acceder a la camera.");
    }
  }

  function stopCamera() {
    if (!stream) {
      return;
    }
    stream.getTracks().forEach((track) => track.stop());
    stream = null;
  }

  function capturePhoto() {
    if (!stream) {
      return;
    }
    photoCtx.drawImage(video, 0, 0, canvas.width, canvas.height);
    photoInput.value = canvas.toDataURL("image/png");
    showCapturedPhoto();
  }

  function retakePhoto() {
    photoInput.value = "";
    showLiveVideo();
  }

  function getSignaturePosition(event) {
    const rect = signatureCanvas.getBoundingClientRect();
    return {
      x: event.clientX - rect.left,
      y: event.clientY - rect.top
    };
  }

  function startSignature(event) {
    isDrawing = true;
    hasSignature = true;
    const pos = getSignaturePosition(event);
    signatureCtx.beginPath();
    signatureCtx.moveTo(pos.x, pos.y);
  }

  function drawSignature(event) {
    if (!isDrawing) {
      return;
    }
    const pos = getSignaturePosition(event);
    signatureCtx.lineTo(pos.x, pos.y);
    signatureCtx.stroke();
  }

  function endSignature() {
    isDrawing = false;
    signatureCtx.closePath();
  }

  function clearSignature() {
    signatureCtx.clearRect(0, 0, signatureCanvas.width, signatureCanvas.height);
    isDrawing = false;
    hasSignature = false;
    signatureInput.value = "";
  }

  function prepareSubmit() {
    if (hasSignature) {
      signatureInput.value = signatureCanvas.toDataURL("image/png");
    }
  }

  startBtn.addEventListener("click", startCamera);
  captureBtn.addEventListener("click", capturePhoto);
  retakeBtn.addEventListener("click", retakePhoto);
  clearSignatureBtn.addEventListener("click", clearSignature);

  signatureCanvas.addEventListener("pointerdown", startSignature);
  signatureCanvas.addEventListener("pointermove", drawSignature);
  signatureCanvas.addEventListener("pointerup", endSignature);
  signatureCanvas.addEventListener("pointerleave", endSignature);

  form.addEventListener("submit", prepareSubmit);
  window.addEventListener("beforeunload", stopCamera);

  showLiveVideo();
})();

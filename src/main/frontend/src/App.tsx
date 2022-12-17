import React, {ChangeEvent, useState} from 'react';
import axios from "axios";
import {saveAs} from "file-saver";
import "./App.css";

function TiffProcessor() {
    const [selectedFile, setSelectedFile] = useState<File>();
    const [errorText, setErrorText] = useState<string>();
    const [processedImageUrl, setProcessedImageUrl] = useState<string>();
    const selectFile = (event: ChangeEvent<HTMLInputElement>) => {
        const files = event.target.files;
        if (files && files.length > 0) {
            setErrorText(undefined);
            setProcessedImageUrl(undefined);
            setSelectedFile(files[0]);
        }
    };
    const submitFile = () => {
        if (selectedFile) {
            const formData = new FormData();
            formData.append(
                "file",
                selectedFile,
                selectedFile?.name
            );
            axios.post('/api/tiff/reduce-noise', formData, {responseType: "blob"}).then(response => {
                const imageUrl = URL.createObjectURL(response.data);
                setProcessedImageUrl(imageUrl);
                saveAs(imageUrl, "processedImage.png");
            }).catch(error => {
                setErrorText(error.toString());
            });
        }
    };

    return (
        <div className={"tiff-processor"}>
            <input type={"file"} onChange={selectFile}/>
            <input type={"submit"} value={"Process"} onClick={submitFile}/>
            <br/>
            <br/>
            <div className={"processed-image"}>{processedImageUrl &&
                <img src={processedImageUrl} alt={"processedImageUrl"}/>}</div>
            {errorText && <text> ${errorText} </text>}
        </div>
    );
}

function App() {
    return (
        <div className="nanolyze-app">
            <h1>Welcome to Nanolyze Tiff Analyzer</h1>
            <p>Upload the tiff file containing multiple images and get a tiff file with a single image with all the
                noise removed</p>
            <TiffProcessor/>
        </div>
    );
}

export default App;

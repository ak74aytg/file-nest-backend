<h2>Overview</h2>
<p>The backend for File Nest handles user authentication, file management, and text extraction. It is built with Spring Boot and provides APIs for seamless interaction with the frontend.</p>

<hr>

<h2>Features</h2>
<ul>
    <li><strong>User Authentication:</strong>
        <ul>
            <li>User registration and login via email.</li>
            <li>Email verification for secure access.</li>
        </ul>
    </li>
    <li><strong>File Management:</strong>
        <ul>
            <li>Upload files.</li>
            <li>View previously uploaded files.</li>
            <li>Delete files.</li>
        </ul>
    </li>
    <li><strong>Text Extraction:</strong>
        <ul>
            <li>Extract text from uploaded files using OCR.</li>
        </ul>
    </li>
</ul>

<hr>

<h2>Tech Stack</h2>
<ul>
    <li><strong>Java</strong> with <strong>Spring Boot</strong></li>
    <li><strong>Spring Security</strong> for authentication and authorization</li>
    <li><strong>Spring Data JPA</strong> with <strong>MySQL</strong> for database management</li>
    <li><strong>Tesseract OCR</strong> for text extraction</li>
    <li><strong>Docker</strong> for containerization</li>
</ul>

<hr>

<h2>API Endpoints</h2>
<table border="1">
    <thead>
        <tr>
            <th>Method</th>
            <th>Endpoint</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>POST</td>
            <td>/api/auth/register</td>
            <td>Register a new user</td>
        </tr>
        <tr>
            <td>POST</td>
            <td>/api/auth/login</td>
            <td>Log in an existing user</td>
        </tr>
        <tr>
            <td>GET</td>
            <td>/api/auth/verify-email</td>
            <td>Verify user email</td>
        </tr>
        <tr>
            <td>GET</td>
            <td>/api/files</td>
            <td>Fetch all files for the logged-in user</td>
        </tr>
        <tr>
            <td>POST</td>
            <td>/api/files/upload</td>
            <td>Upload a new file</td>
        </tr>
        <tr>
            <td>POST</td>
            <td>/api/files/extract-text</td>
            <td>Extract text from a file</td>
        </tr>
        <tr>
            <td>DELETE</td>
            <td>/api/files/{id}</td>
            <td>Delete a file</td>
        </tr>
    </tbody>
</table>

<hr>

<h2>Setup Instructions</h2>
<ol>
    <li>Clone the repository:
        <pre><code>git clone &lt;backend-repo-url&gt;

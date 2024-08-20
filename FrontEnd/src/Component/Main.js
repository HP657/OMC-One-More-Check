import React, { useEffect, useState } from "react";
import axios from "axios";
import "../Css/Main.css";
import MakePostButton from "./MakePostButton";
import { Outlet, useNavigate } from "react-router-dom";

axios.defaults.withCredentials = true;

const Main = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [UserProfileImg, setUserProfileImg] = useState("imgs/person.png")
  const navigate = useNavigate();

  const handleClick = (label) => {
    console.log(`${label} clicked`);
    switch (label) {
      case "Home":
        navigate("/");
        break;
      case "Profile":
        navigate("/mypage");
        break;
    }
};

  const fetchUserInfo = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/auth/info");
      setUserInfo(response.data.data);
      setUserProfileImg(userInfo.profileImgUrl)
    } catch (error) {
      console.error("Error fetching user info: ", error);
    }
  };

  const logout = () => {
    try {
      axios.post("http://localhost:8080/api/auth/logout");
      setUserInfo(null);
      navigate("/signin");
    } catch (error) {
      console.error("Error logging out: ", error);
    }
  };

  useEffect(() => {
    fetchUserInfo();
  }, []);

  return (
    <div className="container">
      <header className="header">
        <div className="logo" onClick={() => handleClick("Home")}>Logo</div>
          <MakePostButton />
        <div className="loginarea">
          {userInfo ? (
            <div>
              <p>Welcome, {userInfo.username}</p>
              <button className="logout-button" onClick={logout}>로그아웃</button>
            </div>
          ) : (
            <div className="button-container">
              <button className="signup-button" onClick={() => { navigate('/signup'); }}>회원가입</button>
              <button className="signin-button" onClick={() => { navigate('/signin'); }}>로그인</button>
            </div>
          )}
        </div>
      </header>
      <main className="content">
        <Outlet />
      </main>
      <footer className="footer">
        <div className="footerItem" onClick={() => handleClick("Home")}>
          <img src="/imgs/home.png" alt="Home" className="img" />
          <button className="footer-button">Home</button>
        </div>
        <div className="footerItem" onClick={() => handleClick("Settings")}>
          <img src="/imgs/setting.png" alt="Settings" className="img" />
          <button className="footer-button">Settings</button>
        </div>
        <div className="footerItem" onClick={() => handleClick("Profile")}>
          <img src={UserProfileImg} alt="Profile" className="img" />
          <button className="footer-button">Profile</button>
        </div>
      </footer>
    </div>
  );
};

export default Main;

import { BrowserRouter, Routes, Route } from "react-router-dom";
import SignUpPage from "./Component/SignUpPage";
import SignInPage from "./Component/SignInPage";
import Main from "./Component/Main";
import MakePost from "./Component/MakePost";
import DetailPost from "./Component/DetailPost";
import ViewContent from "./Component/ViewContent";
import Admin from "./Component/Admin";
import Profile from "./Component/Profile";

function App() {
  const posts = [];

  return (
    <div>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Main />}>
            <Route index element={<ViewContent posts={posts} />} />
            <Route path="/mapo" element={<MakePost />} />
            <Route path="/signup" element={<SignUpPage />} />
            <Route path="/signin" element={<SignInPage />} />
            <Route path="view/post/:postId" element={<DetailPost />} />
            <Route path="/mypage" element={<Profile />} />
          </Route>
          <Route path="/admin" element={<Admin />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;

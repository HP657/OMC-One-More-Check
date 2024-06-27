import React, { useState } from 'react';
import axios from 'axios';
import '../Css/Form.css';

const SignInPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        const data = { email, password };

        try {
            const response = await axios.post(`http://localhost:8080/api/auth/signin`, data, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            console.log('로그인 성공:', response.data);
        } catch (error) {
            console.error('로그인 실패:', error);
            setError('로그인에 실패했습니다. 다시 시도해주세요.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <form onSubmit={handleSubmit} className="auth-form">
                <h2>로그인</h2>
                {error && <p className="error-message">{error}</p>}
                <input 
                    type="email" 
                    placeholder="이메일" 
                    value={email} 
                    onChange={(e) => setEmail(e.target.value)} 
                    required 
                />
                <input 
                    type="password" 
                    placeholder="비밀번호" 
                    value={password} 
                    onChange={(e) => setPassword(e.target.value)} 
                    required 
                />
                <button type="submit" disabled={loading}>
                    {loading ? '로그인 중...' : '로그인'}
                </button>
            </form>
        </div>
    );
};

export default SignInPage;
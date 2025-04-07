package example.shopping.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 自定义未授权处理
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                    Result.error(401, "未授权访问，请先登录")));
        };
    }

    /**
     * 自定义权限不足处理
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, e) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                    Result.error(403, "权限不足，无法访问")));
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 关闭CSRF
            .csrf().disable()
            // 不使用session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // 登录注册接口允许匿名访问
            .antMatchers("/api/auth/**").permitAll()
            // 静态资源放行
            .antMatchers("/", "/*.html", "/favicon.ico", "/**/*.html", "/**/*.css", "/**/*.js", "/images/**").permitAll()
            // 允许对商品的GET请求
            .antMatchers("/api/products/**").permitAll()
            // 允许对分类的GET请求
            .antMatchers("/api/categories/**").permitAll()
            // 除上面外的所有请求全部需要鉴权认证
            .anyRequest().authenticated()
            .and()
            // 配置自定义异常处理
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint())
            .accessDeniedHandler(accessDeniedHandler());

        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 添加CORS支持
        http.cors();
    }
}

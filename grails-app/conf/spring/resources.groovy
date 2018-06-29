import abos.server.CurrentUserTenantResolver
import abos.server.UserPasswordEncoderListener

beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)
    currentUserTenantResolver(CurrentUserTenantResolver)
}

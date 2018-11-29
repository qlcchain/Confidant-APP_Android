package chat.tox.antox.fragments

import android.support.v4.app.Fragment
//import android.support.v4.widget.DrawerLayout


class MainDrawerFragment extends Fragment {

  //private var //mDrawerLayout: DrawerLayout = _
  /*private var mNavigationView: NavigationView = _

  private var preferences: SharedPreferences = _

  private var userDetailsSubscription: Subscription = _

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    preferences = PreferenceManager.getDefaultSharedPreferences(getActivity)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {

    super.onCreateView(inflater, container, savedInstanceState)
    val rootView = inflater.inflate(R.layout.fragment_main_drawer, container, false)

    // Set up the navigation drawer
    ////mDrawerLayout = rootView.findViewById(R.id.drawer_layout).asInstanceOf[DrawerLayout]
    mNavigationView = rootView.findViewById(R.id.left_drawer).asInstanceOf[NavigationView]

    mNavigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener {
      override def onNavigationItemSelected(menuItem: MenuItem): Boolean = {
        selectItem(menuItem)
        true
      }
    })

    val drawerHeader = rootView.findViewById(R.id.drawer_header)

    // zoff //
    if (drawerHeader != null) {
      drawerHeader.setOnClickListener(new OnClickListener {
        override def onClick(v: View): Unit = {
          val intent = new Intent(getActivity, classOf[ProfileSettingsActivity])
          startActivity(intent)
        }
      })
    }

    // zoff //
    if (drawerHeader != null) {
      drawerHeader.setBackgroundColor(ThemeManager.primaryColorDark)
    }

    rootView
  }

  override def onResume(): Unit = {
    super.onResume()

    userDetailsSubscription = State.userDb(getActivity)
      .activeUserDetailsObservable()
      .combineLatestWith(AntoxOnSelfConnectionStatusCallback.connectionStatusSubject)((user, status) => (user, status))
      .observeOn(AndroidMainThreadScheduler())
      .subscribe((tuple) => {
        refreshDrawerHeader(tuple._1, tuple._2)
      })
  }

  def refreshDrawerHeader(userInfo: UserInfo, connectionStatus: ToxConnection): Unit = {
    val avatarView = getView.findViewById(R.id.drawer_avatar).asInstanceOf[CircleImageView]

    val mAvatar = AVATAR.getAvatarFile(userInfo.avatarName, getActivity)

    // zoff //
    if (avatarView != null) {
      mAvatar match {
        case Some(avatar) =>
          BitmapManager.load(avatar, isAvatar = true).foreach(avatarView.setImageBitmap)
        case None =>
          avatarView.setImageResource(R.drawable.default_avatar)
      }
    }

    val nameView = getView.findViewById(R.id.name).asInstanceOf[TextView]

    // zoff //
    if (nameView != null) {
      nameView.setText(new String(userInfo.nickname.value))
    }
    val statusMessageView = getView.findViewById(R.id.status_message).asInstanceOf[TextView]
    // zoff //
    if (statusMessageView != null) {
      statusMessageView.setText(new String(userInfo.statusMessage.value))
    }

    updateNavigationHeaderStatus(connectionStatus)
  }

  def updateNavigationHeaderStatus(toxConnection: ToxConnection): Unit = {
    val statusView = getView.findViewById(R.id.status)

    val status = UserStatus.getToxUserStatusFromString(State.userDb(getActivity).getActiveUserDetails.status)
    val online = toxConnection != ToxConnection.NONE
    val drawable = getResources.getDrawable(IconColor.iconDrawable(online, status))

    // zoff //
    if (statusView != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        statusView.setBackground(drawable)
      } else {
        statusView.setBackgroundDrawable(drawable)
      }
    }
  }

  // isDrawerOpen: Boolean = //mDrawerLayout.isDrawerOpen(GravityCompat.START)

  def openDrawer(): Unit = {
    //mDrawerLayout.openDrawer(GravityCompat.START)
  }

  def closeDrawer(): Unit = {
    //mDrawerLayout.closeDrawer(GravityCompat.START)
  }

  private def selectItem(menuItem: MenuItem) {
    val id = menuItem.getItemId

    menuItem.setChecked(false)
    //mDrawerLayout.closeDrawer(mNavigationView)
  }

  override def onPause(): Unit = {
    super.onPause()

    userDetailsSubscription.unsubscribe()
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
  }*/
}

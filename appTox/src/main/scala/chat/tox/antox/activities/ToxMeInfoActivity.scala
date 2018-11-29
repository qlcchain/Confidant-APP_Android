package chat.tox.antox.activities

import android.support.v7.app.AppCompatActivity

class ToxMeInfoActivity extends AppCompatActivity {

  /*protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_toxme_info)
    getSupportActionBar.setHomeButtonEnabled(true)
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    getWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow.setStatusBarColor(Color.parseColor("#202020"))
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && getSupportActionBar != null) {
      val info = new ActivityManager.RunningTaskInfo()
      getSupportActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#24221f")))
    }

    val toxMeWebsite = findViewById(R.id.toxme_info_website).asInstanceOf[TextView]

    toxMeWebsite.setMovementMethod(LinkMovementMethod.getInstance)
    toxMeWebsite.setText(Html.fromHtml(getResources.getString(R.string.toxme_website)))

    val sourceURLTextView = findViewById(R.id.toxme_source).asInstanceOf[TextView]
    val pattern = Pattern.compile("https://github.com/LittleVulpix/toxme")
    Linkify.addLinks(sourceURLTextView, pattern, "")
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        finish()
        true
      case _ =>
        super.onOptionsItemSelected(item)
    }
  }*/

}
